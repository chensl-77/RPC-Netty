package csl.netty.nio.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: csl
 * @DateTime: 2022/7/6 17:33
 **/
public class TestFilesWalkTree {
    public static void main(String[] args) throws IOException {
        //遍历文件夹
        WalkTree();
        //删除文件夹
        Delete();
    }

    private static void Delete() throws IOException {
        Files.walkFileTree(Paths.get("D:\\桌面\\学习\\java复习\\csl"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //删除文件
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                //删除子文件夹
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void WalkTree() throws IOException {
        final AtomicInteger dircount = new AtomicInteger();
        final AtomicInteger filecount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\桌面\\学习\\java复习"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dircount.incrementAndGet();
                System.out.println("dir=====>"+dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                filecount.incrementAndGet();
                System.out.println("file"+file);
                return super.visitFile(file, attrs);
            }
        });
        System.out.println(dircount);
        System.out.println(filecount);
    }
}
