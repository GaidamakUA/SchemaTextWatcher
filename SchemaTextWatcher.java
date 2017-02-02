package com.blogspot.androidgaidamak.utility;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GaidamakUA on 2/2/17.
 */
public class SchemaTextWatcher implements TextWatcher {
    private static final String TAG = "SchemaTextWatcher";
    private static final Pattern VALID_SCHEMA = Pattern.compile("[_|\\s]+");
    private static final Pattern SINGLE_DIGIT = Pattern.compile(".*(\\d)");
    @NonNull
    private final Set<Integer> mSpacesPositions;
    @NonNull
    private final EditText mPhoneEditText;
    private final int MAX_LENGTH;

    private boolean isEditing = false;

    private int mStart;
    private int mLengthBefore;
    private int mLengthAfter;
    private String mFormattedText;
    private List<Character> mDigits;
    private int mRemoveDigitOnIndex;

    public SchemaTextWatcher(@NonNull EditText phoneEditText) {
        this(phoneEditText, phoneEditText.getText().toString());
    }

    public SchemaTextWatcher(@NonNull EditText phoneEditText, @NonNull String schema) {
        mSpacesPositions = new HashSet<>();
        MAX_LENGTH = schema.length();

        mPhoneEditText = phoneEditText;
        mPhoneEditText.setSelection(0);

        parseSchema(schema);
    }

    private void parseSchema(@NonNull String schema) {
        validateSchema(schema);
        initSpacesPositions(schema);
    }

    private void validateSchema(@NonNull String schema) {
        if (!VALID_SCHEMA.matcher(schema).matches()) {
            throw new IllegalArgumentException("Illegal schema. Schema can consist of \'_\' and \' \'");
        }
    }

    private void initSpacesPositions(@NonNull String schema) {
        for (int i = 0; i < schema.length(); i++) {
            if (schema.charAt(i) == ' ') {
                mSpacesPositions.add(i);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int lengthBefore, int lengthAfter) {
        if (isCharRemoved() && charSequence.charAt(start) == ' ') {
            mRemoveDigitOnIndex = start - 1;
        } else {
            mRemoveDigitOnIndex = -1;
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int lengthBefore, int lengthAfter) {
        if (isEditing) {
            return;
        }

        isEditing = true;
        mStart = start;
        mLengthBefore = lengthBefore;
        mLengthAfter = lengthAfter;
        mDigits = createCharacterArray(charSequence);

        mFormattedText = formatText(charSequence);
        mPhoneEditText.setText(mFormattedText);
        setCursorPosition();

        isEditing = false;
    }

    @NonNull
    private String formatText(CharSequence charSequence) {
        StringBuilder sb = new StringBuilder(MAX_LENGTH);
        for (int i = 0; i < MAX_LENGTH; i++) {
            sb.append(getNextCharacter(i));
        }
        return sb.toString();
    }

    private void setCursorPosition() {
        Matcher matcher = SINGLE_DIGIT.matcher(mFormattedText);
        int lastDigitPosition = matcher.find() ? matcher.end() : 0;

        if (isCharAdded()) {
            if (mStart >= lastDigitPosition && lastDigitPosition < MAX_LENGTH) {
                mPhoneEditText.setSelection(lastDigitPosition);
            } else if (lastDigitPosition == MAX_LENGTH && mStart >= lastDigitPosition) {
                mPhoneEditText.setSelection(lastDigitPosition);
            } else {
                if (mFormattedText.charAt(mStart) == ' ') {
                    mPhoneEditText.setSelection(mStart + 2);
                } else {
                    mPhoneEditText.setSelection(mStart + 1);
                }
            }
        } else {
            if (mFormattedText.charAt(mStart) == ' ') {
                mPhoneEditText.setSelection(mStart - 1);
            } else {
                mPhoneEditText.setSelection(mStart);
            }
        }
    }

    private boolean isCharAdded() {
        return mLengthAfter > mLengthBefore;
    }

    private boolean isCharRemoved() {
        return !isCharAdded();
    }

    public char getNextCharacter(int index) {
        if (!mSpacesPositions.contains(index) && !mDigits.isEmpty() && index == mRemoveDigitOnIndex) {
            mDigits.remove(0);
        }
        if (mSpacesPositions.contains(index)) {
            return ' ';
        } else if (!mDigits.isEmpty()) {
            Character digit = mDigits.get(0);
            mDigits.remove(0);
            return digit;
        } else {
            return '_';
        }
    }

    private List<Character> createCharacterArray(CharSequence charSequence) {
        String digits = StringHelper.getDigits(charSequence.toString());
        ArrayList<Character> list = new ArrayList<>(digits.length());
        for (int i = 0; i < digits.length(); i++) {
            list.add(digits.charAt(i));
        }
        return list;
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
