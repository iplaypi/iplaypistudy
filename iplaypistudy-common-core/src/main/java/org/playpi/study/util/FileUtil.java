package org.playpi.study.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * 文件工具类
 * 读/写
 */
@Slf4j
public class FileUtil {

    /**
     * 获取对应的excel版本的workbook
     * 不支持解析的文件格式则抛出异常
     * 读取完成后关闭输入流
     *
     * @param inputPath
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static Workbook createWorkbook(String inputPath) throws IOException, InvalidFormatException {
        InputStream inputStream = new FileInputStream(inputPath);
        if (!inputStream.markSupported()) {
            inputStream = new PushbackInputStream(inputStream, 8);
        }
        // xls格式
        if (POIFSFileSystem.hasPOIFSHeader(inputStream)) {
            return new HSSFWorkbook(inputStream);
        }
        // xlsx格式
        if (POIXMLDocument.hasOOXMLHeader(inputStream)) {
            return new XSSFWorkbook(OPCPackage.open(inputPath));
        }
        // 此时可以关闭输入流,因为内容已经读取到Workbook实例里面了
        inputStream.close();
        throw new IllegalArgumentException("!!!!excel版本目前解析不了");
    }

}
