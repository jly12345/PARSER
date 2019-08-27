package com.symbio.epb.bigfile.model.enums;

import com.symbio.epb.bigfile.error.BaseBusinessModuleException;

/**
 * 
 * @author Yao Pan
 *
 */
public enum SheetNameType {
    HIERARCHY("HIERARCHY",0,"roster"), MOST("MOST",1,"most"),
    ODREPEATS("OD-REPEATS",2,"odrepeats"), ADJ("ADJ",3,"adj"),UG("UG",4,"ug");

    private String typeName;
    private int index;
    private String fileKey;


    private SheetNameType(String typeName,int index,String fileKey) {
        this.typeName = typeName;
        this.index = index;
        this.fileKey = fileKey;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getIndex() {
        return index;
    }

    public String getFileKey() {
        return fileKey;
    }

    public static SheetNameType getTypeByName(String name){
        for(SheetNameType type: SheetNameType.values()){
            if(type.getTypeName().equals(name)){
                return type;
            }
        }
        throw new BaseBusinessModuleException("not support SheetNameType "+ name);
    }
}
