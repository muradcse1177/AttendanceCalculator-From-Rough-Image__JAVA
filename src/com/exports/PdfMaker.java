package com.exports;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.rmi.CORBA.StubDelegate;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.attendence.*;
public class PdfMaker {
	private Document document;
	private FileOutputStream outputFile;
	private ArrayList<StudentInfo> studentInfo;
	private BufferedImage sheetHeader;
	private Image sheetHeaderImage;
	
	public PdfMaker(ArrayList<StudentInfo>stdInfo, BufferedImage sheetHeader, String outputPath) throws BadElementException, IOException {
		outputFile = new FileOutputStream(outputPath);
		studentInfo = stdInfo;
		this.sheetHeader = sheetHeader;

	}
	

	public void makePdf() throws DocumentException {
		
		document = new Document();
		
		PdfWriter pdfWriter = PdfWriter.getInstance(document, outputFile);
		PdfPCell cell;
		document.open();
	
		PdfPTable table = new PdfPTable(10);
		
		
		cell = new PdfPCell(new Phrase("Sl No."));
		cell.setColspan(1);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Class \nRoll No."));
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Name"));
		// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setColspan(5);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Attendance"));
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell);
		
		for(int i=0; i<studentInfo.size(); i++){
			
		
			
			
			cell = new PdfPCell(new Phrase((i+1)+"."));
			cell.setColspan(1);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			

			cell = new PdfPCell(new Phrase(studentInfo.get(i).getRoll()));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(cell);

			cell = new PdfPCell(new Phrase(studentInfo.get(i).getName()));
			// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setColspan(5);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(studentInfo.get(i).getAttendance()+"%"));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(cell);
		}
		
		
		
		document.add(table);
		document.close();
	}


}
