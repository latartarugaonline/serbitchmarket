package it.ltm.scp.module.android.devices.printer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.ltm.scp.module.android.exceptions.InvalidArgumentException;
import it.ltm.scp.module.android.model.devices.printer.gson.Data;
import it.ltm.scp.module.android.model.devices.printer.gson.DataRow;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.model.devices.printer.gson.InputCustomDataRow;
import it.ltm.scp.module.android.model.devices.printer.gson.InputDataRow;
import it.ltm.scp.module.android.model.devices.printer.gson.InputDataRowItem;
import it.ltm.scp.module.android.utils.CustomColumnOrderComparator;

/**
 * Created by HW64 on 25/08/2016.
 */
public class DocumentBuilderImpl implements DocumentBuilder {

    private Document document;
    private DocumentBuilderHelper builderHelper;
    private List<Data> operationList;

    private int copyTextIndex;
    private Data copyTextData;

    public DocumentBuilderImpl() {
        builderHelper = new DocumentBuilderHelper();
        operationList = new ArrayList<>();
    }

    public void appendData(Data data) {
        operationList.add(data);
    }

    @Override
    public void setTextForCopies(String text) {
        copyTextIndex = operationList.size();
        copyTextData = new Data.Builder()
                .op(Data.OP_TEXT)
                .value(text)
                .build();
    }

    @Override
    public void setRow(String input) {
        InputDataRow inputDataRow = new Gson().fromJson(input, InputDataRow.class);
        int columnFactor = inputDataRow.getColumnGroup();
        List<DataRow> columns = new ArrayList<>();
        for (InputDataRowItem item : inputDataRow.getColumns()) {
            columns.add(new DataRow(columnFactor, item));
        }
        this.operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_ROW)
                .value(columns)
                .build());
    }

    @Override
    public void setCustomRow(String inputData) {
        List<InputCustomDataRow> columnList = new Gson().fromJson(inputData, new TypeToken<List<InputCustomDataRow>>() {
        }.getType());
        Collections.sort(columnList, new CustomColumnOrderComparator());
        List<DataRow> columns = new ArrayList<>();
        String font = DefaultPrinterConfig.getFont();   // inizializzo con font di default
        for (InputCustomDataRow column : columnList) {
            columns.add(new DataRow(column));
            font = column.getFont() != null ? column.getFont() : font; //FIXME fix se font non è settato (null)
        }
        try {
            setFont(font);  // settare custom font della riga
        } catch (InvalidArgumentException e) {
            Log.e(DocumentBuilderImpl.class.getSimpleName(), "setCustomRow: ", e);
        } finally {
            this.operationList.add(new Data.Builder()
                    .op(Data.OP_CMD)
                    .type(Data.TYPE_ROW)
                    .value(columns)
                    .build());
        }
    }

    @Override
    public void setEol() {
        this.operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_EOL)
                .build());
    }

    @Override
    public void setNewLines(int lines) {
        for (int i = 0; i < lines; i++) {
            operationList.add(new Data.Builder()
                    .op(Data.OP_TEXT)
                    .value(" ")
                    .build());
            setEol();
        }
    }

    @Override
    public void setSize(int size) throws InvalidArgumentException {
        builderHelper.setSize(size);
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_SIZE)
                .value(size)
                .build());
    }

    @Override
    public void setText(String text) {
        // mettere nuovo parametro boolean per verificare se andare a capo o meno. in caso positivo appendere setEol() direttamente
        operationList.add(new Data.Builder()
                .op(Data.OP_TEXT)
                .value(text)
                .build());
    }

    @Override
    public void setFeed(int value, String direction) throws InvalidArgumentException {
        builderHelper.setFeed(value, direction);
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_FEED)
                .value(value)
                .build());
    }

    @Override
    public void setCut(boolean part) {
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_CUT)
                .part(part)
                .build());
    }

    @Override
    public void setFont(String fontType) throws InvalidArgumentException {
        builderHelper.setFont(fontType);
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_FONT)
                .value(fontType)
                .build());
    }

    @Override
    public void setFlip(boolean flip) {
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_FLIP)
                .value(flip)
                .build());
    }

    @Override
    public void setNegative(boolean negative) {
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_NEGATIVE)
                .value(negative)
                .build());
    }

    @Override
    public void setRotate(boolean rotate) {
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_ROTATE)
                .value(rotate)
                .build());
    }

    @Override
    public void setLine(int value) throws InvalidArgumentException {
        builderHelper.setLine(value);
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_LINE)
                .value(value)
                .build());
    }

    @Override
    public void setSpacing(int spacing) throws InvalidArgumentException {
        builderHelper.setSpacing(spacing);
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_SPACE)
                .value(spacing)
                .build());
    }

    @Override
    public void setAlign(String align) throws InvalidArgumentException {
        builderHelper.setAlign(align);
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_ALIGN)
                .value(align)
                .build());
    }

    @Override
    public void setBold(boolean bold) {
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_BOLD)
                .value(bold)
                .build());
    }

    @Override
    public void setUnderline(int value) throws InvalidArgumentException {
        builderHelper.setUnderline(value);
        operationList.add(new Data.Builder()
                .op(Data.OP_STYLE)
                .option(Data.OPTION_UNDERLINE)
                .value(value)
                .build());
    }

    @Override
    public void tab() {
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_TAB)
                .build());
    }

    @Override
    public void setBarcode(@NonNull String code, String bctype, int width, int height, String font, String position, String codeset) throws InvalidArgumentException {
        builderHelper.setBarcode(code, bctype, width, height, font, position, codeset);
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_BARCODE)
                .code(code)
                .bctype(bctype)
                .width(width)
                .height(height)
                .font(font)
                .position(position)
                .codeset(codeset)
                .build());
    }

    @Override
    public void setImage(String format, String align, int width, int height, String encoded, int zoom) throws InvalidArgumentException {
        builderHelper.setImage(format, align, width, height, encoded, zoom);
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_IMAGE)
                .format(format)
                .align(align)
                .width(width)
                .height(height)
                .encoded(encoded)
                .zoom(zoom)
                .build());
    }

    @Override
    public void setQrCode(String code, int model, int errors, int mode, int size) throws InvalidArgumentException {
        builderHelper.setQrCode(code, model, errors, mode, size);
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_QRCODE)
                .code(code)
                .model(model)
                .errors(errors)
                .mode(mode)
                .size(size)
                .build());
    }

    @Override
    public void setMatrix(String code) throws InvalidArgumentException {
        builderHelper.setMatrix(code);
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_MATRIX)
                .code(code)
                .build());
    }

    @Override
    public void setMaxicode(String code, int mode) throws InvalidArgumentException {
        builderHelper.setMaxicode(code, mode);
        operationList.add(new Data.Builder()
                .op(Data.OP_CMD)
                .type(Data.TYPE_MAXICODE)
                .code(code)
                .mode(mode)
                .build());
    }

    public Document build() {
        document = new Document();
        document.addData(operationList);
        return document;
    }

    public Document buildCopy() {
        if (copyTextIndex > -1 && copyTextData != null) {
            operationList.add(copyTextIndex, copyTextData);
            copyTextIndex = -1;
            copyTextData = null;
        }
        return build();
    }

    public void clear() {
        operationList.clear();
    }

    public DocumentBuilderImpl getBuilder() {
        return this;
    }
}