/*
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

package com.sun.jna.platform.win32.COM.office;

import com.sun.jna.platform.win32.COM.*;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef;

import static com.sun.jna.platform.win32.Variant.VARIANT.VARIANT_MISSING;

public class MSExcel extends COMLateBindingObject {

    public MSExcel() throws COMException {
        super("Excel.Application", false);
    }

    public MSExcel(boolean visible) throws COMException {
        this();
        this.setVisible(visible);
    }

    public static String columnNumberStr(int columnNumber) {
        String colStr = "";
        while(columnNumber >= 0) {
            colStr += (char)('A'+columnNumber%26);
            columnNumber = columnNumber/26 - 1;
        }
        return colStr;
    }

    public void setVisible(boolean bVisible) throws COMException {
        this.setProperty("Visible", bVisible);
    }

    public String getVersion() throws COMException {
        return this.getStringProperty("Version");
    }

    public void newExcelBook() throws COMException {
        this.invokeNoReply("Add", getWorkbooks());
    }

    public void openExcelBook(String filename) throws COMException {
        this.invokeNoReply("Open", getWorkbooks().getIDispatch(), new VARIANT[] {
                new VARIANT(filename),
                new VARIANT(1), // Update Links
                VARIANT_MISSING, //ReadOnly
                VARIANT_MISSING, // Format
                VARIANT_MISSING, // Password
                VARIANT_MISSING, // WriteResPassword
                VARIANT_MISSING, // Ignore ReadOnly Recommended
                VARIANT_MISSING, // Origin
                VARIANT_MISSING, // Delimiter
                VARIANT_MISSING, // Editable
                VARIANT_MISSING, // Notify
                VARIANT_MISSING, // Converter
                VARIANT_MISSING, // AddToMru
                VARIANT_MISSING, // Local
                new VARIANT(0) // CorruptLoad
        });
    }

    public void closeActiveWorkbook(boolean bSave) throws COMException {
        this.invokeNoReply("Close", getActiveWorkbook(), new VARIANT(bSave));
    }

    public void quit() throws COMException {
        this.invokeNoReply("Quit");
    }

    public void disableAskUpdateLinks() {
        this.setProperty("AskToUpdateLinks", false);
    }

    public void insertValue(String range, String value) throws COMException {
        Range pRange = new Range(this.getAutomationProperty("Range",
                this.getActiveSheet(), new VARIANT(range)), range);
        this.setProperty("Value", pRange, new VARIANT(value));
    }

    public Application getApplication() {
        return new Application(this.getAutomationProperty("Application"));
    }

    public Workbook getActiveWorkbook() {
        return new Workbook(this.getAutomationProperty("ActiveWorkbook"));
    }

    public Workbooks getWorkbooks() {
        return new Workbooks(this.getAutomationProperty("WorkBooks"));
    }

    public ActiveSheet getActiveSheet() {
        return new ActiveSheet(this.getAutomationProperty("ActiveSheet"));
    }

    public static class Application extends COMLateBindingObject {

        public Application(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public static class Workbooks extends COMLateBindingObject {
        public Workbooks(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }

        public int count() {
            return getIntProperty("Count");
        }
    }

    public static class Workbook extends COMLateBindingObject {
        public Workbook(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }

        public void enableUpdateLinks() {
            this.setProperty("UpdateLinks", new VARIANT(true));
        }

        public void enableUpdateRemote() {
            this.setProperty("UpdateRemote", new VARIANT(true));
        }

        public void forceDataUpdate() {
            this.setProperty("ForceFullCalculation", new VARIANT(true));
        }

        public Sheets getSheets() {
            return new Sheets(this.getAutomationProperty("Worksheets"));
        }
    }

    public static class Sheets extends COMLateBindingObject {

        public Sheets(IDispatch iDispatch) {
            super(iDispatch);
        }

        public int size() {
            return this.getIntProperty("Count");
        }

        public Sheet getSheet(int i) {
            return new Sheet(this.getAutomationProperty("Item", new VARIANT(i)));
        }
    }

    public static class Sheet extends COMLateBindingObject {

        public Sheet(IDispatch iDispatch) {
            super(iDispatch);
        }

        public String name() {
            return getStringProperty("Name");
        }

        public Range cells() {
            return new Range(getAutomationProperty("Cells"), null);
        }
    }

    public class ActiveSheet extends COMLateBindingObject {
        public ActiveSheet(IDispatch iDispatch) throws COMException {
            super(iDispatch);
        }
    }

    public static class Range extends COMLateBindingObject {
        private final String identifier;

        public Range(IDispatch iDispatch, String identifier) throws COMException {
            super(iDispatch);
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public int height() {
            return getIntProperty("Height");
        }

        public int width() {
            return getIntProperty("Width");
        }

        public Range getRange(String range) {
            return new Range(getAutomationProperty("Range", new VARIANT(range)), range);
        }

        public String value() {
            VARIANT.ByReference result = new VARIANT.ByReference();
            this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, "Value");
            return Helper.variantToString(result);
        }

        public String getError() {
            VARIANT.ByReference result = new VARIANT.ByReference();
            this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, "Value");
            Object value = result.getValue();
            if(value instanceof WinDef.SCODE) {
                return Helper.errorToString((WinDef.SCODE) value);
            }
            return null;
        }

        public boolean isError() {
            VARIANT.ByReference result = new VARIANT.ByReference();
            this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, "Value");
            Object value = result.getValue();
            return value instanceof WinDef.SCODE;
        }
    }
}
