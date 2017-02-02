# SchemaTextWatcher
TextWatcher to handle input with empty spaces '_' and placeholders.
This functionality is great to have for credit cards and passwords input.

## Usage example
To use the library you should copy and paste class SchemaTextWatcher and use it like this:
### First example
```java
editText = (EditText) view.findViewById(R.id.card_number);
editText.addTextChangedListener(new SchemaTextWatcher(editText, "____ ____ ____ ____"));
```
### Second example
Alternatively you can use  it like this:
```java
editText = (EditText) view.findViewById(R.id.card_number);
editText.addTextChangedListener(new SchemaTextWatcher(editText));
```
And in layou you should set text as a future scheme for layout"
```xml
    <EditText
        android:id="@+id/card_number"
        style="@style/TextAppearance.DefaultText.EditText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@android:color/transparent"
        android:text="____ ____ ____ ____"
        android:maxLength="20"/>
```
## Limitations:
For now only '_' for digits and ' ' for placeholders supported. And for now it can only work with digits. But I did my best to nicely structure code and make it extendable. So you can add your functianality by yourself.
