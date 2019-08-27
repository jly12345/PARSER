package com.symbio.epb.bigfile.handler;

import com.symbio.epb.bigfile.event.ParseCompleteEvent;
import com.symbio.epb.bigfile.model.enums.BigFileType;
import com.symbio.epb.bigfile.model.enums.ParseFileType;
import com.symbio.epb.bigfile.model.enums.XssfDataType;
import com.symbio.epb.bigfile.pojo.DomainLobSite;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
import com.symbio.epb.bigfile.service.PageDataService;
import com.symbio.epb.bigfile.service.UploadDataSyncLogService;
import com.symbio.epb.bigfile.utils.MyFileUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The<code>Class  SiteFilePaserHandler </code>
 *
 * @author benju.xie
 * @since 2018/9/4
 */
@Component
@Scope("prototype")
public class SiteFilePaserHandler extends DefaultHandler {
    public static Logger logger = LoggerFactory.getLogger(SiteFilePaserHandler.class);

    private SharedStringsTable sst;
    private int sheetIndex = -1;
    private List<String> rowlist = new ArrayList<String>();
    private int curRow = 0;
    private int curCol = 0;
//    private String domainLobSitePath;
    private StringBuffer errMsg = new StringBuffer();
    private List<List<String>> siteSourceData = new ArrayList<>();
    private List<String> siteHeader;
//    private Map<String, List<String>> domainLobSiteMapping;
    private List<DomainLobSite> domainlobsiteList;
    private String fileDate;
    @Value("${epb.bigfile.site-file-path}")
    private String splitFilePath;
	@Value("${epb.bigfile.uploader-username}")
	private String username;
	@Value("${epb.bigfile.uploader-password}")
	private String password;
    private StringBuffer contents;
    private XssfDataType nextDataType;
    private String preRef = null;
    private String ref = null;
    private String maxRef = null;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/M/d");
    private SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss a");
    private long dataDate;
    @Resource
    private ApplicationContext applicationContext;
    @Value("${epb.dfs-root}")
    private String dfsRoot;
    @Value("${epb.bucket-name}")
    private String bucketName;
    @Value("${epb.dfs-site-pre}")
    private String dfsSitePre;
    @Autowired
    private UploadDataSyncLogService uploadDataSyncLogService;
    private final static String  SITEDATAPRE = "sitedata";
    @Autowired
    private PageDataService domainLobSiteService;
	private long parseLogId;



    public void setSplitFilePath(String splitFilePath) {
        this.splitFilePath = splitFilePath;
    }

    public void process(InputStream inputStream) throws Exception {
//        this.kafkaTemplate =kafkaTemplate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        dataDate = sdf.parse(fileDate).getTime();
//        loadLobSiteMappingList();
        domainlobsiteList = domainLobSiteService.findAllDomainLobSite();
        if (domainlobsiteList==null || domainlobsiteList.size()==0) {
        	logger.info("There is no domain_lob_site data.");
			return;
		}
        this.contents = new StringBuffer();
        this.nextDataType = XssfDataType.FORMULA;
        OPCPackage pkg = OPCPackage.open(inputStream);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            curRow = 0;
            sheetIndex++;
            InputStream data = sheets.next();
            InputSource sheetSource = new InputSource(data);
            try {
                parser.parse(sheetSource);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                errMsg.append("sheet:" + sheetIndex + " data:" + ref + " parser error;");
            }
            data.close();
        }

        if (errMsg.length() > 0)
            return;

       //TODO siteDataSyncLogService.deleteByDataDate(dataDate);
        for ( DomainLobSite domainLobSite : domainlobsiteList) {
            createSiteSource(domainLobSite);
        }
        pkg.flush();
        pkg.close();
        siteSourceData.clear();
    }
    public void processUpload(InputStream inputStream) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	OPCPackage pkg = null;
    	try {
	    	dataDate = sdf.parse(fileDate).getTime();
	    	domainlobsiteList = domainLobSiteService.findAllDomainLobSite();
	    	if (domainlobsiteList==null || domainlobsiteList.size()==0) {
	    		logger.info("There is no domain_lob_site data.");
	    		return;
	    	}
	    	this.contents = new StringBuffer();
	    	this.nextDataType = XssfDataType.FORMULA;
	    	pkg = OPCPackage.open(inputStream);
	    	XSSFReader r = new XSSFReader(pkg);
	    	SharedStringsTable sst = r.getSharedStringsTable();
	    	XMLReader parser = fetchSheetParser(sst);
	    	Iterator<InputStream> sheets = r.getSheetsData();
	    	while (sheets.hasNext()) {
	    		curRow = 0;
	    		sheetIndex++;
	    		InputStream data = null;
	    		try {
		    		data = sheets.next();
		    		InputSource sheetSource = new InputSource(data);
		    		parser.parse(sheetSource);
				} finally {
					if (data != null) {
						data.close();
					}
				}
	    	}
    	} catch (Exception ex) {
    		logger.error(ex.getMessage());
    		errMsg.append("sheet:" + sheetIndex + " data:" + ref + " parser error;");
    	} finally {
    		pkg.flush();
    		if (pkg != null) {
    			try {
					pkg.close();
				} catch (IOException e) {
					logger.error("OPCPackage closed error: " + e.getMessage());
				}
			}
		}
    	if (errMsg.length() > 0) {
    		logger.info("Parse error: " + errMsg.toString());
    		return;
    	}
    	
    	for ( DomainLobSite domainLobSite : domainlobsiteList) {
    		createSiteSource(domainLobSite);
    	}
    	siteSourceData.clear();
    	//通知自动上传开始
    	Map<String, Object> param = new HashMap<>();
    	param.put("parseLogId", parseLogId);
    	param.put("fileType", BigFileType.SITE.getValue());
    	ParseCompleteEvent event = new ParseCompleteEvent(param);
    	applicationContext.publishEvent(event);
    }
    
    private void createSiteSource(DomainLobSite domainLobSite) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("PPRO");
        //  XSSFRow headerRow = sheet.createRow(0);
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < siteHeader.size()-1; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(siteHeader.get(i+1));
        }

        List<List<String>> siteData = siteSourceData.stream().filter(d -> d.get(0).equals(domainLobSite.getSite())).collect(Collectors.toList());
        for(int i=0;i<siteData.size();i++){
            XSSFRow row = sheet.createRow(i + 1);
            List<String> data = siteData.get(i);
            for(int j=1;j<data.size();j++){
                XSSFCell cell = row.createCell(j-1);
                String value = "";
                if (!StringUtils.isEmpty(data.get(j)) && !(data.get(j)).contains("#N/A") &&
                        !(data.get(j)).contains("NA") &&
                        !(data.get(j)).contains("N/A"))
                {

                    value = data.get(j);
                }
                cell.setCellValue(value);
            }
        }
        String dfsFilePath =  saveExcel(SITEDATAPRE, domainLobSite, workbook);
    	
    	if(!CollectionUtils.isEmpty(siteData)){
    		String[] lobsite = domainLobSite.getLobSite().split("_");
    		String lobName = lobsite[0].toLowerCase();
    		String siteName = lobsite[1].toLowerCase();
    		String domainName = domainLobSite.getDomain();
    		String fileName = SITEDATAPRE+"_"+domainLobSite.getLobSite() + "_" + fileDate + ".xlsx";
    		UploadDataSyncLog siteDataSyncLog = new UploadDataSyncLog();
    		siteDataSyncLog.setLobName(lobName);
    		siteDataSyncLog.setSiteName(siteName);
    		siteDataSyncLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
    		siteDataSyncLog.setFileDate(new Timestamp(dataDate));
    		siteDataSyncLog.setFilePath(dfsFilePath);
    		siteDataSyncLog.setDomainName(domainName);
    		siteDataSyncLog.setFileName(fileName);
    		siteDataSyncLog.setUploader(username);
    		siteDataSyncLog.setParseLogId(parseLogId);
    		siteDataSyncLog.setType(ParseFileType.SITE.getValue());
    		uploadDataSyncLogService.save(siteDataSyncLog);
//    		UploadFileDataDto siteDataDto = new UploadFileDataDto();
//    		siteDataDto.setSyncLogId(siteDataSyncLog.getId());
//    		siteDataDto.setLobName(lobName);
//    		siteDataDto.setSiteName(siteName);
//    		siteDataDto.setDomainName(domainName);
//    		siteDataDto.setDataDate(dataDate);
//    		siteDataDto.setFileName(fileName);
//    		siteDataDto.setFilePath(dfsFilePath);
//    		siteDataDto.setFileType(UploadFileType.PERFORMANCE.getValue());
//    		siteDataDto.setUploader(username);
//    		siteDataDto.setPassword(password);
//    		siteDataDto.setAttemptNumber(0);
//    		ObjectMapper objectMapper = new ObjectMapper();
//    		String data = null;
//			try {
//				data = objectMapper.writeValueAsString(siteDataDto);
//			} catch (JsonProcessingException e) {
//				logger.error("Json process error during sitedata file parse: "+ e.getMessage());
//			}
//			String topic = "data." + lobName;
//    		kafkaTemplate.send(topic, data);
//    		logger.info("send topic:"+topic);
    	}
        
    }
    
   

    private String saveExcel(String type, DomainLobSite domainLobSite, XSSFWorkbook workbook) {
        String[] lobsite = domainLobSite.getLobSite().split("_");
        String lobName = lobsite[0].toUpperCase();
        String domainName = domainLobSite.getDomain();
        String dirPath = splitFilePath + File.separator + fileDate + File.separator + domainName + File.separator + lobName + File.separator;
        MyFileUtil.mkDirs(new File(dirPath));
        String filePath = dirPath + type +"_" + domainLobSite.getLobSite() + "_" + fileDate + ".xlsx";
        File file = new File(filePath);
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			workbook.write(fos);
			if (workbook != null) {
				workbook.close();
			}
			return filePath;
		} catch (IOException e) {
			logger.error("IOException during sitedata file parse: "+e.getMessage());
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error("IOException during sitedata file parse: "+e.getMessage());
			}
		}
        return null;
    }
    
   
    



//    private void loadLobSiteMapping() {
//        if (domainLobSiteMapping == null) {
//            domainLobSiteMapping = new HashMap<>();
//            try {
//                InputStream is = new FileInputStream(domainLobSitePath);
//                XSSFWorkbook xwb = new XSSFWorkbook(is);
//                XSSFSheet sheet = xwb.getSheetAt(0);
//                XSSFRow row;
//                for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
//                    row = sheet.getRow(i);
//                    String site = null;
//                    String lob = null;
//                    String domain = null;
//                    for (int j = row.getFirstCellNum(); j < 3; j++) {
//                        if (j == 0) {
//                            site = getCell(row, j);
//                        } else if (j == 1) {
//                            lob = getCell(row, j);
//                        } else {
//                            domain = getCell(row, j);
//                        }
//                    }
//                    List<String> data = new ArrayList<>();
//                    data.add(lob);
//                    data.add(domain);
//                    domainLobSiteMapping.put(site, data);
//                }
//            } catch (Exception e) {
//                logger.error(e.getMessage());
//            }
//        }
//    }




    public void optRow(int sheetIndex, int curRow, List<String> rowList) {
        if (!CollectionUtils.isEmpty(rowList)) {
            if (sheetIndex == 0) {
                List<String> dataList = new ArrayList<>();
                if (curRow == 0) {
                    dataList.addAll(rowList);
                    siteHeader = new ArrayList<>();
                    siteHeader.addAll(rowList);
                    dataList.clear();
                }
                if (curRow > 0) {
                    dataList.addAll(rowList);
                    siteSourceData.add(dataList);
                }
            }

        }
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
                XMLReaderFactory.createXMLReader(
                        "org.apache.xerces.parsers.SAXParser"
                );
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;

    }

    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        if ("c".equals(name)) {
            //前一个单元格的位置
            if (preRef == null) {
                preRef = attributes.getValue("r");
            } else {
                preRef = ref;
            }
            //当前单元格的位置
            ref = attributes.getValue("r");
            String cellType = attributes.getValue("t");
            String cellSomething = attributes.getValue("s");
            if ("b".equals(cellType))
                nextDataType = XssfDataType.BOOL;
            else if ("e".equals(cellType))
                nextDataType = XssfDataType.FORMULA;
            else if ("s".equals(cellType))
                nextDataType = XssfDataType.SSTINDEX;
            else if ("str".equals(cellType)) {
                nextDataType = XssfDataType.FORMULA;
            }
            else if ("3".equals(cellSomething))
                nextDataType = XssfDataType.NUMBER;
            else if ("8".equals(cellSomething))
                nextDataType = XssfDataType.NUMBER;
            else {
                nextDataType = XssfDataType.FORMULA;
            }
        }
        contents.setLength(0);
    }


    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (localName.equals("worksheet")) {
            rowlist.clear();
            optRow(sheetIndex, 1, rowlist);
            curRow = 0;
        }

        String thisStr = null;
        if ("v".equals(name)) {
            switch (nextDataType) {
                case BOOL:
                    char first = contents.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                case DATE:
                    double daysSince = Double.parseDouble(contents.toString());
                    Date d = DateUtil.getJavaDate(daysSince);
                    thisStr = simpleDateFormat.format(d);
                    break;
                case DATETIME:
                    Date dt = DateUtil.getJavaDate(Double.parseDouble(contents.toString()));
                    thisStr = dt.toString();
                    break;
                case SSTINDEX:
                    String sstIndex = contents.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rts = new XSSFRichTextString(sst.getEntryAt(idx));
                        thisStr = rts.toString();
                    } catch (NumberFormatException ex) {
                        logger.error("Pgmr err, lastContents is not int: " + sstIndex);
                    }
                    break;
                case TIME:
                    Date t = DateUtil.getJavaDate(Double.parseDouble(contents.toString()));
                    thisStr = simpleTimeFormat.format(t);
                    break;

                case FORMULA:
                    thisStr = contents.toString();
                    break;

                case NUMBER:
                    String[] strs = contents.toString().split("\\.");
                    StringBuffer pattern = new StringBuffer("0.0");
                    if (strs.length > 1) {
                        for (int i = 1; i < strs[1].length(); i++) {
                            pattern.append("0");
                        }
                    }
                    String str = new DecimalFormat(pattern.toString()).format(new BigDecimal(contents.toString()));
                    double number = Double.parseDouble(str);
                    int num = (int) number;
                    if (num == number) {
                        thisStr = String.valueOf(num);
                    } else {
                        thisStr = String.valueOf(number);
                    }
                    break;
                default:
                    thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
            }

            if (!ref.equals(preRef)) {
                int len = countNullCell(ref, preRef);
                for (int i = 0; i < len; i++) {
                    rowlist.add(curCol, "");
                    curCol++;
                }
            }

            rowlist.add(curCol, thisStr);
            curCol++;

        } else if (name.equals("c")) {
            if (contents.length() == 0) {
                rowlist.add(curCol, "");
                curCol++;
                if (!ref.equals(preRef)) {
                    int len = countNullCell(ref, preRef);
                    for (int i = 0; i < len; i++) {
                        rowlist.add(curCol, "");
                        curCol++;
                    }
                }
            }
        } else {
            if (name.equals("row")) {
                if (curRow == 0) {
                    maxRef = ref;
                }
                //补全一行尾部可能缺失的单元格
                if (maxRef != null) {
                    int len = countNullCell(maxRef, ref);
                    for (int i = 0; i <= len; i++) {
                        rowlist.add(curCol, "");
                        curCol++;
                    }
                }
                optRow(sheetIndex, curRow, rowlist);
                rowlist.clear();
                curRow++;
                curCol = 0;
                preRef = null;
                ref = null;
            }
        }
    }

    public int countNullCell(String ref, String preRef) {
        //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
        String xfd = ref.replaceAll("\\d+", "");
        String xfd_1 = preRef.replaceAll("\\d+", "");

        xfd = fillChar(xfd, 3, '@', true);
        xfd_1 = fillChar(xfd_1, 3, '@', true);

        char[] letter = xfd.toCharArray();
        char[] letter_1 = xfd_1.toCharArray();
        int res = (letter[0] - letter_1[0]) * 26 * 26 + (letter[1] - letter_1[1]) * 26 + (letter[2] - letter_1[2]);
        return res - 1;
    }

    public String fillChar(String str, int len, char let, boolean isPre) {
        int len_1 = str.length();
        if (len_1 < len) {
            if (isPre) {
                for (int i = 0; i < (len - len_1); i++) {
                    str = let + str;
                }
            } else {
                for (int i = 0; i < (len - len_1); i++) {
                    str = str + let;
                }
            }
        }
        return str;
    }


    public void characters(char[] ch, int start, int length) {
        contents.append(ch, start, length);
    }


//    private int nameToColumn(String name) {
//        int column = -1;
//        for (int i = 0; i < name.length(); ++i) {
//            int c = name.charAt(i);
//            column = (column + 1) * 26 + c - 'A';
//        }
//        return column;
//    }


    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public StringBuffer getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(StringBuffer errMsg) {
        this.errMsg = errMsg;
    }

    public long getDataDate() {
        return dataDate;
    }

    public void setDataDate(long dataDate) {
        this.dataDate = dataDate;
    }

	public long getParseLogId() {
		return parseLogId;
	}

	public void setParseLogId(long parseLogId) {
		this.parseLogId = parseLogId;
	}
}
