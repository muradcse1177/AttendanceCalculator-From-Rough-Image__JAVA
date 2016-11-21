package com.exports;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.attendence.StudentInfo;

public class TxtMaker {
	private File file;
	private ArrayList<StudentInfo>studentInfo;
	public TxtMaker(ArrayList<StudentInfo>stdInfo, String outputPath) {
		file = new File(outputPath);
		studentInfo = stdInfo;
	}
	public void makeTxtFile(){
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(String.format("%-7s%-9s%-30s%-8s\n", "Sl No.", "Roll No.", "Name", "Attn."));
			writer.write(String.format("--------------------------------------------------------\n"));

			for (int i = 0; i < studentInfo.size(); i++) {
				writer.write(String.format("%-7d%-9s%-30s%d%s\n", i + 1, studentInfo.get(i).getRoll(),
						studentInfo.get(i).getName(), 60, "%"));
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
