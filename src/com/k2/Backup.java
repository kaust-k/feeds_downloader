package com.k2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class Backup {
	private static final String BASE_DIR = "backup";
	private static final String FEEDS_DIR = BASE_DIR + "/feeds";
	private static final String DB_DIR = BASE_DIR + "/db";
	private static final int MAX_BACKUPS = 5;

	public void performBackup() {
		createBackupDirs();
		copyDbFile();
		moveFeedFileToBackup();
		moveFile(new File(Constants.DUMMY_FEEDS_FILE), new File(Constants.FEEDS_FILE));
		deleteExtraFiles(FEEDS_DIR);
		deleteExtraFiles(DB_DIR);
	}

	private void createBackupDirs() {
		System.out.println("Creating " + FEEDS_DIR);
		(new File(FEEDS_DIR)).mkdirs();

		System.out.println("Creating " + DB_DIR);
		(new File(DB_DIR)).mkdirs();
	}

	private void copyDbFile() {
		try {
			copyFile(new File(Constants.DB_NAME), new File(DB_DIR + "/" + Constants.DB_NAME + "." + getCurrentDateTime()));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		System.out.println("Copying " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	private void moveFeedFileToBackup() {
		File file = new File(Constants.FEEDS_FILE);
		moveFile(file, new File(FEEDS_DIR + "/" + file.getName() + "." + getCurrentDateTime()));
	}

	private void moveFile(File file, File destFile) {
		System.out.println("Moving " + file.getAbsolutePath() + " to " + destFile.getAbsolutePath());
		try {
			file.renameTo(destFile);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void deleteExtraFiles(String directory) {
		File files[] = sortByModifiedDate(new File(directory));
		if (files.length > MAX_BACKUPS) {
			for (int i = 0; i < files.length - MAX_BACKUPS; i++) {
				if (files[i].delete())
					System.out.println("Deleted file " + files[i].getPath());
			}
		}
	}

	private File[] sortByModifiedDate(File directory) {
		File[] files = directory.listFiles();

		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f1.lastModified()).compareTo(
						f2.lastModified());
			}
		});

		return files;
	}
	
	private String getCurrentDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
		return sdf.format(new Date());
	}
}
