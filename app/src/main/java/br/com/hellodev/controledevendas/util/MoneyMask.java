package br.com.hellodev.controledevendas.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MoneyMask implements TextWatcher {

    private final EditText mEditText;
    private boolean isUpdating;

    public MoneyMask(EditText editText) {
        mEditText = editText;
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = unmask(s.toString());
        String mascara;

        if (isUpdating) {
            isUpdating = false;
            return;
        }

        int valorInt = 0;
        try {
            valorInt = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        float valorDividido = (float) (valorInt / 100.0);
        DecimalFormat format = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("pt", "BR"));
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        mascara = format.format(valorDividido);

        isUpdating = true;

        mEditText.setText(mascara);
        mEditText.setSelection(mascara.length());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    public static String unmask(String s) {
        Set<String> replaceSymbols = new HashSet<>();
        replaceSymbols.add("-");
        replaceSymbols.add(".");
        replaceSymbols.add("/");
        replaceSymbols.add("_");
        replaceSymbols.add("(");
        replaceSymbols.add(")");
        replaceSymbols.add(" ");
        replaceSymbols.add(",");
        replaceSymbols.add("R");
        replaceSymbols.add("$");
        replaceSymbols.add(" ");

        for (String symbol : replaceSymbols) {
            s = s.replaceAll("[" + symbol + "]", "");
        }

        return s;
    }

    public static float getValue(String valorText) {
        NumberFormat moneyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        moneyFormatter.setMinimumFractionDigits(1);
        moneyFormatter.setMaximumFractionDigits(1);
        moneyFormatter.setCurrency(Currency.getInstance("BRL"));
        float valorFloat;
        if (valorText.isEmpty()) {
            valorFloat = 0f;
        } else {
            try {
                valorFloat = moneyFormatter.parse(valorText).floatValue();

            } catch (Exception e) {
                e.printStackTrace();
                valorFloat = 0f;
            }
        }
        return valorFloat;
    }
}