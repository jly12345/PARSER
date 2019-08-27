package com.symbio.epb.bigfile.error;

/**
 * The<code>Class  IError </code>
 *
 * @author benju.xie
 * @since 2018/8/13
 */
public interface IError  {
    String getNamespace();

    String getErrorCode();

    String getErrorMessage();
}
