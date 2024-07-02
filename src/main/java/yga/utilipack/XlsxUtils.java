package yga.utilipack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility class for working with XLSX files.
 */
public class XlsxUtils {
	
	static Logger logger = LogManager.getLogger(XlsxUtils.class);

	/**
	 * Reads an Excel file and returns a Workbook object.
	 *
	 * @param filePath the path to the Excel file
	 * @return the Workbook object
	 * @throws IOException if an I/O error occurs
	 */
	public static Workbook readExcelFile(String filePath) throws IOException {
		try (FileInputStream fis = new FileInputStream(filePath)) {
			return new XSSFWorkbook(fis);
		}
	}

	/**
	 * Extracts the sheet name from a file name based on parentheses.
	 *
	 * @param fileName the file name
	 * @param start    the character marking the start of the sheet name
	 * @param end      the character marking the end of the sheet name
	 * @return the extracted sheet name, or null if not found
	 */
	public static String extractSheetName(String fileName, char start, char end) {
		int startIndex = fileName.indexOf(start);
		int endIndex = fileName.indexOf(end);
		if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
			return fileName.substring(startIndex + 1, endIndex);
		}
		return null;
	}

	/**
	 * Writes a Workbook object to an Excel file.
	 *
	 * @param workbook the Workbook object
	 * @param filePath the path to the output Excel file
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeExcelFile(Workbook workbook, String filePath) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(filePath)) {
			workbook.write(fos);
		}
	}

	/**
	 * Copies rows from a source sheet to a target sheet, appending data rows
	 * without overwriting existing content. Applies styles and conditional
	 * formatting.
	 *
	 * @param sourceSheet        the source sheet
	 * @param targetSheet        the target sheet
	 * @param outputWorkbook     the output Workbook object
	 * @param columnsToCheck     the columns to check based on header values
	 * @param usedSheetNames     the set of used sheet names
	 * @param includeEmptySheets whether to include sheets without data rows (only
	 *                           header)
	 */
	public static void copyRowsToOutputSheet(Sheet sourceSheet, Sheet targetSheet, Workbook outputWorkbook,
			String[] columnsToCheck, Set<String> usedSheetNames, boolean includeEmptySheets) {

		// Create a map of header values to column indices
		Map<String, Integer> headerMap = getHeaderMap(sourceSheet);

		// Copy header row with styles if it's not already in targetSheet
		Row sourceHeaderRow = sourceSheet.getRow(0);
		Row targetHeaderRow = targetSheet.getRow(0);
		if (sourceHeaderRow != null && targetHeaderRow == null) {
			targetHeaderRow = targetSheet.createRow(0);
			copyRowWithStyles(sourceHeaderRow, targetHeaderRow, outputWorkbook, headerMap, columnsToCheck);
		}

		// Apply auto filter to header row in the target sheet
		applyAutoFilter(targetSheet);

		// Determine the starting index for new rows in targetSheet
		int lastRowIndex = targetSheet.getLastRowNum();
		int rowIndex = lastRowIndex >= 0 ? lastRowIndex + 1 : 1; // Start new rows after existing content or after
																	// header row if no content

		int rowsCopied = 0; // Counter for the number of data rows copied

		// Copy data rows with styles and conditional formatting
		for (Row sourceRow : sourceSheet) {
			if (sourceRow.getRowNum() == 0) {
				continue; // Skip header row
			}
			if (isTargetRow(sourceRow, columnsToCheck, headerMap)) {
				Row targetRow = targetSheet.createRow(rowIndex);
				copyRowWithStyles(sourceRow, targetRow, outputWorkbook, headerMap, columnsToCheck);
				rowIndex++;
				rowsCopied++;
			}
		}

		// Determine if the sheet should be added to usedSheetNames based on
		// includeEmptySheets
		if (includeEmptySheets || rowsCopied > 0) {
			usedSheetNames.add(targetSheet.getSheetName());
		}
	}

	/**
	 * Retrieves a map of header values to their column indices.
	 *
	 * @param sheet the sheet to read the header from
	 * @return a map of header values to column indices
	 */
	private static Map<String, Integer> getHeaderMap(Sheet sheet) {
		Map<String, Integer> headerMap = new HashMap<>();
		Row headerRow = sheet.getRow(0);
		if (headerRow != null) {
			for (Cell cell : headerRow) {
				String normalizedHeader = normalizeHeaderName(cell.getStringCellValue());
				headerMap.put(normalizedHeader, cell.getColumnIndex());
			}
		}
		return headerMap;
	}

	/**
	 * Normalizes a header name by removing spaces and converting to lower case.
	 *
	 * @param headerName the header name to normalize
	 * @return the normalized header name
	 */
	private static String normalizeHeaderName(String headerName) {
		return headerName.trim().replaceAll("\\s+", "").toLowerCase();
	}

	/**
	 * Checks if a row meets the criteria based on the columns to check.
	 *
	 * @param sourceRow      the source row
	 * @param columnsToCheck the columns to check based on header values
	 * @param headerMap      the map of header values to column indices
	 * @return true if the row meets the criteria, false otherwise
	 */
	private static boolean isTargetRow(Row sourceRow, String[] columnsToCheck, Map<String, Integer> headerMap) {
		for (String columnHeader : columnsToCheck) {
			String normalizedHeader = normalizeHeaderName(columnHeader);
			Integer columnIndex = headerMap.get(normalizedHeader);
			if (columnIndex != null && columnIndex >= 0) { // Ensure columnIndex is valid
				Cell cell = sourceRow.getCell(columnIndex);
				if (cell == null || cell.getCellType() == CellType.BLANK || cell.getStringCellValue().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Applies conditional formatting to a workbook based on specified columns.
	 *
	 * @param workbook           the Workbook object
	 * @param columnsToCheck     the columns to check for conditional formatting
	 * @param includeEmptySheets whether to include sheets without data rows
	 */
	public static void applyConditionalFormattingToWorkbook(Workbook workbook, String[] columnsToCheck,
			boolean includeEmptySheets) {
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);
			Map<String, Integer> headerMap = getHeaderMap(sheet);
			boolean hasDataRows = false;
			for (String columnHeader : columnsToCheck) {
				String normalizedHeader = normalizeHeaderName(columnHeader);
				Integer columnIndex = headerMap.get(normalizedHeader);
				if (columnIndex != null && columnIndex >= 0) { // Ensure columnIndex is valid
					for (int j = 1; j <= sheet.getLastRowNum(); j++) { // Start from the second row (index 1)
						Row row = sheet.getRow(j);
						if (row != null) {
							Cell firstCell = row.getCell(0);
							if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
								continue; // Skip this row if the first cell is empty
							}
							Cell cell = row.getCell(columnIndex);
							if (cell == null) {
								cell = row.createCell(columnIndex);
							}
							applyConditionalFormatting(cell, workbook);
							hasDataRows = true;
						}
					}
				}
			}

			// Remove the sheet if it does not have data rows and includeEmptySheets is
			// false
			if (!includeEmptySheets && !hasDataRows) {
				workbook.removeSheetAt(i);
				i--; // Adjust the index since we removed the current sheet
			}
		}
	}

	/**
	 * Applies conditional formatting to a cell.
	 *
	 * @param cell     the cell to apply conditional formatting to
	 * @param workbook the Workbook object
	 */
	private static void applyConditionalFormatting(Cell cell, Workbook workbook) {
		Sheet sheet = cell.getSheet();
		SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

		// Create rule 1: RED fill if cell is empty ("")
		ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL, "\"\"");
		PatternFormatting fill1 = rule1.createPatternFormatting();
		fill1.setFillBackgroundColor(IndexedColors.RED.getIndex());
		fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

		// Create rule 2: LIGHT_GREEN fill if cell is not empty ("")
		ConditionalFormattingRule rule2 = sheetCF.createConditionalFormattingRule(ComparisonOperator.NOT_EQUAL, "\"\"");
		PatternFormatting fill2 = rule2.createPatternFormatting();
		fill2.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		fill2.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

		CellRangeAddress[] regions = { CellRangeAddress.valueOf(cell.getAddress().formatAsString()) };

		sheetCF.addConditionalFormatting(regions, rule1, rule2);
	}

	/**
	 * Applies auto filter to the header row of the specified sheet.
	 *
	 * @param sheet the sheet to apply the auto filter to
	 */
	private static void applyAutoFilter(Sheet sheet) {
		Row headerRow = sheet.getRow(0);
		if (headerRow != null) {
			int lastCellNum = headerRow.getLastCellNum();
			CellRangeAddress range = CellRangeAddress
					.valueOf(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 0, lastCellNum - 1)
							.formatAsString());
			sheet.setAutoFilter(range);
		}
	}

	/**
	 * Copies a row with styles from the source row to the target row.
	 *
	 * @param sourceRow      the source row
	 * @param targetRow      the target row
	 * @param outputWorkbook the output Workbook object
	 * @param headerMap      the map of header values to column indices
	 * @param columnsToCheck the columns to check based on header values
	 */
	private static void copyRowWithStyles(Row sourceRow, Row targetRow, Workbook outputWorkbook,
			Map<String, Integer> headerMap, String[] columnsToCheck) {
		targetRow.setHeight(sourceRow.getHeight());

		for (int columnIndex = sourceRow.getFirstCellNum(); columnIndex < sourceRow.getLastCellNum(); columnIndex++) {
			Cell sourceCell = sourceRow.getCell(columnIndex);
			Cell targetCell = targetRow.createCell(columnIndex);

			if (sourceCell != null) {
				// Copy cell value based on its type
				switch (sourceCell.getCellType()) {
				case STRING:
					targetCell.setCellValue(sourceCell.getStringCellValue());
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(sourceCell)) {
						targetCell.setCellValue(sourceCell.getDateCellValue());
					} else {
						targetCell.setCellValue(sourceCell.getNumericCellValue());
					}
					break;
				case BOOLEAN:
					targetCell.setCellValue(sourceCell.getBooleanCellValue());
					break;
				case FORMULA:
					targetCell.setCellFormula(sourceCell.getCellFormula());
					break;
				case BLANK:
					targetCell.setBlank();
					break;
				default:
					break;
				}

				// Copy cell style
				CellStyle sourceCellStyle = sourceCell.getCellStyle();
				CellStyle targetCellStyle = outputWorkbook.createCellStyle();
				targetCellStyle.cloneStyleFrom(sourceCellStyle);
				targetCell.setCellStyle(targetCellStyle);

				// Copy cell comments
				if (sourceCell.getCellComment() != null) {
					copyCellComment(sourceCell, targetCell, outputWorkbook);
				}

				// Copy hyperlinks
				if (sourceCell.getHyperlink() != null) {
					targetCell.setHyperlink(sourceCell.getHyperlink());
				}
			}
		}
	}

	/**
	 * Copies a cell comment from a source cell to a target cell.
	 *
	 * @param sourceCell     the source cell
	 * @param targetCell     the target cell
	 * @param outputWorkbook the output Workbook object
	 */
	private static void copyCellComment(Cell sourceCell, Cell targetCell, Workbook outputWorkbook) {
		Drawing<?> drawing = targetCell.getSheet().createDrawingPatriarch();

		// Remove existing comment if any
		if (targetCell.getCellComment() != null) {
			targetCell.removeCellComment();
		}

		Comment sourceComment = sourceCell.getCellComment();
		CreationHelper factory = outputWorkbook.getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();

		anchor.setCol1(targetCell.getColumnIndex());
		anchor.setCol2(targetCell.getColumnIndex() + 3); // Adjust column span as needed
		anchor.setRow1(targetCell.getRowIndex());
		anchor.setRow2(targetCell.getRowIndex() + 5); // Adjust row span as needed

		Comment targetComment = drawing.createCellComment(anchor);

		RichTextString str = factory.createRichTextString(sourceComment.getString().getString());
		targetComment.setString(str);
		targetComment.setAuthor(sourceComment.getAuthor());

		targetCell.setCellComment(targetComment);
	}

	/**
	 * Converts a CSV file to an Excel file.
	 *
	 * @param directoryPath the directory path for input and output files
	 * @param fileName      the name of the CSV file to convert
	 * @throws IOException if an I/O error occurs
	 */
	public static void convertCSVtoXLSX(String directoryPath, String fileName) throws IOException {
		String csvFilePath = directoryPath + fileName;
		try (FileInputStream inputStream = new FileInputStream(csvFilePath);
				XSSFWorkbook workbook = new XSSFWorkbook()) {

			XSSFSheet sheet = workbook.createSheet("Sheet1");
			int rowNum = 0;

			try (Scanner scanner = new Scanner(inputStream)) {
				while (scanner.hasNextLine()) {
					String[] data = scanner.nextLine().split(CsvUtils.getSeparator() + "");
					XSSFRow row = sheet.createRow(rowNum++);
					int colNum = 0;

					for (String field : data) {
						XSSFCellStyle style = workbook.createCellStyle();
						style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						XSSFCell cell = row.createCell(colNum++);
						cell.setCellValue(field);
					}
				}
			}

			String xlsFilePath = csvFilePath.replace(".csv", ".xlsx");
			try (FileOutputStream outputStream = new FileOutputStream(new File(xlsFilePath))) {
				workbook.write(outputStream);
			}
		}
		logger.info("Conversion of file " + fileName + " completed.");
	}

}
