package com.blankj.utilcode.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.blankj.utilcode.utils.ConstUtils.KB;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/11
 *     desc  : 文件相关的工具类
 * </pre>
 */
public class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    /**
     * @param closeable closeable
     */
    private static void closeIO(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        return StringUtils.isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code false}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code false}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return {@code false}: 是<br>{@code false}: 否
     */
    public static boolean isDir(String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(File file) {
        return isFileExists(file) && file.isDirectory();
    }

    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(File file) {
        return isFileExists(file) && file.isFile();
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() && file.isDirectory() || file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        if (file.exists() && file.isFile()) return true;
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath 文件路径
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(File file) {
        if (file == null) return false;
        // 文件存在并且删除失败返回false
        if (file.exists() && file.isFile() && !file.delete()) return false;
        // 创建目录失败返回false
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制或移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param isMove      是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveDir(String srcDirPath, String destDirPath, boolean isMove) {
        return copyOrMoveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), isMove);
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @param isMove  是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveDir(File srcDir, File destDir, boolean isMove) {
        if (srcDir == null || destDir == null) return false;
        // 源文件不存在或者不是目录则返回false
        if (!srcDir.exists() || !srcDir.isDirectory()) return false;
        // 目标目录不存在返回false
        if (!createOrExistsDir(destDir)) return false;
        File[] srcDirFiles = srcDir.listFiles();
        for (File srcDirFile : srcDirFiles) {
            File oneDestFile = new File(destDir.getPath() + File.separator
                    + srcDirFile.getName());
            if (srcDirFile.isFile()) {
                copyOrMoveFile(srcDirFile, oneDestFile, isMove);
            } else if (srcDirFile.isDirectory()) {
                copyOrMoveDir(srcDirFile, oneDestFile, isMove);
            }
            if (isMove && !deleteFile(srcDirFile)) return false;
        }
        return true;
    }

    /**
     * 复制或移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param isMove       是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile(String srcFilePath, String destFilePath, boolean isMove) {
        return copyOrMoveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), isMove);
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param isMove   是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile(File srcFile, File destFile, boolean isMove) {
        if (srcFile == null || destFile == null) return false;
        // 源文件不存在或者不是文件则返回false
        if (!srcFile.exists() || !srcFile.isFile()) return false;
        // 目标文件存在且是文件则返回false
        if (destFile.exists() && destFile.isFile()) return false;
        // 目标目录不存在返回false
        if (!createOrExistsDir(destFile.getParentFile())) return false;
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile), false) && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public boolean copyDir(String srcDirPath, String destDirPath) {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
    }

    /**
     * 复制目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(File srcDir, File destDir) {
        return copyOrMoveDir(srcDir, destDir, false);
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @return {@code true}: 复制拷贝成功<br>{@code false}: 复制失败
     */
    public static boolean copyFile(String srcFilePath, String destFilePath) {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath));
    }

    /**
     * 复制文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyFile(File srcFile, File destFile) {
        return copyOrMoveFile(srcFile, destFile, false);
    }

    /**
     * 移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public boolean moveDir(String srcDirPath, String destDirPath) {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
    }

    /**
     * 移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveDir(File srcDir, File destDir) {
        return copyOrMoveDir(srcDir, destDir, true);
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveFile(String srcFilePath, String destFilePath) {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath));
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveFile(File srcFile, File destFile) {
        return copyOrMoveFile(srcFile, destFile, true);
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public boolean deleteDir(String dirPath) {
        return deleteDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public boolean deleteDir(File dir) {
        if (dir == null) return false;
        if (!dir.exists()) return true;
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (!deleteFile(file)) return false;
            } else if (file.isDirectory()) {
                if (!deleteDir(file)) return false;
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromIS(String filePath, InputStream is, boolean append) {
        return writeFileFromIS(getFileByPath(filePath), is, append);
    }

    /**
     * 将输入流写入文件
     *
     * @param file   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromIS(File file, InputStream is, boolean append) {
        if (file == null || is == null) return false;
        if (!createOrExistsFile(file)) return false;
        OutputStream os = null;
        try {
            os = new FileOutputStream(file, append);
            byte data[] = new byte[KB];
            while (is.read(data) != -1) os.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeIO(is);
            closeIO(os);
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(String filePath, String content, boolean append) {
        return writeFileFromString(getFileByPath(filePath), content, append);
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) return false;
        if (!createOrExistsFile(file)) return false;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeIO(fileWriter);
        }
    }

    /**
     * 简单获取文件编码格式
     *
     * @param filePath 文件路径
     * @return 文件编码
     */
    public static String getFileCharsetSimple(String filePath) {
        return getFileCharsetSimple(getFileByPath(filePath));
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    public static String getFileCharsetSimple(File file) {
        int p = 0;
        BufferedInputStream bin = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(file));
            p = (bin.read() << 8) + bin.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bin);
        }
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    /**
     * 获取文件行数
     *
     * @param filePath 文件路径
     * @return 文件行数
     */
    public static int getFileLines(String filePath) {
        return getFileLines(getFileByPath(filePath));
    }

    /**
     * 获取文件行数
     *
     * @param file 文件
     * @return 文件行数
     */
    public static int getFileLines(File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[KB];
            int readChars;
            while ((readChars = is.read(buffer)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (buffer[i] == '\n') ++count;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is);
        }
        return count;
    }

    /**
     * 按行读取文件
     *
     * @param file 文件
     * @return 行链表
     */
    public static List<String> readFileByLine(File file) {
        return readFileByLine(file, null);
    }

    /**
     * 按行读取文件
     *
     * @param file        文件
     * @param charsetName 字符编码格式
     * @return 行链表
     */
    public static List<String> readFileByLine(File file, String charsetName) {
        if (file == null) return null;
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;
        try {
            if (charsetName == null) {
                reader = new BufferedReader(new FileReader(file));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(reader);
        }
        return list;
    }

    /**
     * 读取前几行数据
     *
     * @param file       文件
     * @param endLineNum 需要读取的行数
     * @return 包含制定行的list
     */
    public static List<String> readFileByLine(File file, int endLineNum) {
        if (file == null) return null;
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if (list.size() == endLineNum) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(reader);
        }
        return list;
    }

    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        if (!file.isFile()) return null;
        return readFile(file, charsetName);
    }

    public static StringBuilder readFile(File file, String charsetName) {
        StringBuilder sb = new StringBuilder("");
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!sb.toString().equals("")) {
                    sb.append("\r\n");
                }
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb;
    }


    /**
     * @param filePath
     * @param charsetName
     * @return
     */
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        if (!file.isFile()) return null;
        List<String> fileContent = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * byte单位转换（单位：unit）
     *
     * @param size 大小
     * @param unit <ul>
     *             <li>ConstUtils.BYTE:字节</li>
     *             <li>ConstUtils.KB  :千字节</li>
     *             <li>ConstUtils.MB  :兆</li>
     *             <li>ConstUtils.GB  :GB</li>
     *             </ul>
     * @return 大小以unit为单位
     */
    public static double byte2Unit(long size, int unit) {
        switch (unit) {
            case ConstUtils.BYTE:
            case ConstUtils.KB:
            case ConstUtils.MB:
            case ConstUtils.GB:
                return (double) size / unit;
        }
        return -1;
    }

    /**
     * 获取文件大小
     * <p>例如：getFileSize(filePath, ConstUtils.MB); 返回文件大小单位为MB</p>
     *
     * @param filePath 文件路径
     * @param unit     <ul>
     *                 <li>ConstUtils.BYTE:字节</li>
     *                 <li>ConstUtils.KB  :千字节</li>
     *                 <li>ConstUtils.MB  :兆</li>
     *                 <li>ConstUtils.GB  :GB</li>
     *                 </ul>
     * @return 文件大小以unit为单位
     */
    public static double getFileSize(String filePath, int unit) {
        return getFileSize(getFileByPath(filePath), unit);
    }

    /**
     * 获取文件大小
     * <p>例如：getFileSize(file, ConstUtils.MB); 返回文件大小单位为MB</p>
     *
     * @param file 文件
     * @param unit <ul>
     *             <li>ConstUtils.BYTE:字节</li>
     *             <li>ConstUtils.KB  :千字节</li>
     *             <li>ConstUtils.MB  :兆</li>
     *             <li>ConstUtils.GB  :GB</li>
     *             </ul>
     * @return 文件大小以unit为单位
     */
    public static double getFileSize(File file, int unit) {
        if (!isFileExists(file)) return -1;
        return byte2Unit(file.length(), unit);
    }

    /**
     * 获取文件路径的父级目录
     *
     * @param filePath
     * @return
     */
    public static String getDirName(String filePath) {
        if (StringUtils.isSpace(filePath)) return filePath;
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
    }

    public static String getFileName(String filePath) {
        if (StringUtils.isSpace(filePath)) return filePath;
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    public static String getFileNameNoExtension(String filePath) {
        if (StringUtils.isSpace(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    public static String getFileExtension(String filePath) {
        if (StringUtils.isSpace(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }
}