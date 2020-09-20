package com.ffm.lms.report.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

@Component
public class ReportPdfGenerator {

	@Value("${file.upload.path}")
	private String filePath;

	@Value("${file.upload.path.url}")
	private String filePathUrl;

	public String generatePdfFromHtml(String html, String fileName) throws DocumentException, IOException {
		fileName = fileName+System.currentTimeMillis() + ".pdf";
		String outputFolder = filePath + File.separator + fileName;
		OutputStream outputStream = new FileOutputStream(outputFolder);
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(html);
		renderer.layout();
		renderer.createPDF(outputStream);
		outputStream.close();

		return ServletUriComponentsBuilder.fromCurrentContextPath().path(filePathUrl).path(fileName).toUriString();
	}
}
