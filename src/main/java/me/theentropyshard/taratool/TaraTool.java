package me.theentropyshard.taratool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaraTool {
    private static class FileEntry {
        public String relativeName;
        public File file;

        FileEntry(String relativeName, File file) {
            this.relativeName = relativeName;
            this.file = file;
        }
    }

    private static class ListEntry {
        public String fileName;
        public int fileSize;

        ListEntry(String fileName, int fileSize) {
            this.fileName = fileName;
            this.fileSize = fileSize;
        }
    }

    private static void collectFiles(File sourceDir, String baseName, List<FileEntry> collector) {
        File[] files = sourceDir.listFiles();
        if(files == null) {
            return;
        }
        for(File file : files) {
            String realName;
            if(baseName == null) {
                realName = file.getName();
            } else {
                realName = baseName + File.separator + file.getName();
            }
            if(file.isDirectory()) {
                TaraTool.collectFiles(file, realName, collector);
            } else if(file.isFile()) {
                collector.add(new FileEntry(realName, file));
            }
        }
    }

    private static void packTara(String inputDirName, String outputFileName) throws IOException {
        File sourceDir = new File(inputDirName);
        List<FileEntry> fileEntries = new ArrayList<>();
        TaraTool.collectFiles(sourceDir, null, fileEntries);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFileName));
        dos.writeInt(fileEntries.size());
        for(FileEntry fileEntry : fileEntries) {
            System.out.println("Writing header: " + fileEntry.relativeName + ", " + fileEntry.file.length());
            dos.writeUTF(fileEntry.relativeName);
            dos.writeInt((int) fileEntry.file.length());
        }
        for(FileEntry fileEntry : fileEntries) {
            byte[] bytes = new byte[(int) fileEntry.file.length()];
            FileInputStream fis = new FileInputStream(fileEntry.file);
            if(fis.read(bytes) < fileEntry.file.length()) {
                throw new RuntimeException("File read error. File name: " + fileEntry.relativeName);
            }
            fis.close();
            dos.write(bytes);
            System.out.println("File " + fileEntry.relativeName + " data has been written");
        }
        dos.close();
    }

    private static void unpackTara(String inputFileName, String outputDirName) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(inputFileName));
        List<ListEntry> files = new ArrayList<>();
        int numFiles = dis.readInt();
        for(int i = 0; i < numFiles; i++) {
            String fileName = dis.readUTF();
            int fileSize = dis.readInt();
            files.add(new ListEntry(fileName, fileSize));
        }
        File file = new File(outputDirName);
        if(!file.exists() && !file.mkdirs()) {
            throw new IOException("Unable to create output directory");
        }
        System.out.println("Unpacking " + numFiles + " files to " + file.getAbsolutePath());
        for(ListEntry entry : files) {
            System.out.println(entry.fileName + " [" + entry.fileSize + "]");
            byte[] bytes = new byte[entry.fileSize];
            int read = dis.read(bytes);
            if(read > 0) {
                FileOutputStream fis = new FileOutputStream(new File(outputDirName, entry.fileName));
                fis.write(bytes, 0, read);
                fis.close();
            }
        }
    }

    private static void usage() {
        String b = "Usage: java -jar TaraTool.jar <mode> <input/output file> <input/output folder>\n" +
                "  Modes:\n" +
                "    pack - Pack tara\n" +
                "      Args: <input folder> <output file>\n" +
                "    unpack - Unpack tara\n" +
                "      Args: <input file> <output folder>\n";

        System.err.println(b);
    }

    public static void main(String[] args) {
        switch(args.length) {
            case 0:
                System.err.println("No args specified");
                TaraTool.usage();
                break;
            case 1:
                String mode = args[0];
                if(mode.equals("pack") || mode.equals("unpack")) {
                    System.err.println("Only mode specified");
                    TaraTool.usage();
                } else {
                    System.err.println("Unknown mode '" + mode + "'");
                    TaraTool.usage();
                }
                break;
            case 2:
                TaraTool.usage();
                break;
            case 3:
                String useMode = args[0];
                try {
                    if(useMode.equals("pack")) {
                        TaraTool.packTara(args[1], args[2]);
                    } else if(useMode.equals("unpack")) {
                        TaraTool.unpackTara(args[1], args[2]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.err.println("Too many args specified");
                TaraTool.usage();
                break;
        }
    }
}
