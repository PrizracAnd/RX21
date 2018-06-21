package demjanov.av.ru.rx21;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Main_View {
    private final static String IN_PROGRESS = "IN_PROGRESS";

    EditText textFileName;
    MyDialog myDialog;
    Converter converter;
    Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElements();
    }

    private void initializeElements() {
        textFileName = (EditText)findViewById(R.id.textFileName);
        myDialog = new MyDialog();
        converter = new Converter(this);
        convertButton = (Button)findViewById(R.id.buttonConvert);
        convertButton.setOnClickListener(this);
    }

    @Override
    public void setIsComplete(boolean isOK) {
        if(myDialog.isVisible()) myDialog.dismiss();
    }

    public void dialogCancelClicked(){
        converter.stopConvert();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonConvert:
                startConvert();
                break;
            default:
                break;
        }
    }

    private void startConvert() {
        String fileName = textFileName.getText().toString();
        if(!fileName.equals("")){
            converter.convert(fileName);

            FragmentManager manager = getSupportFragmentManager();
            myDialog.show(manager, IN_PROGRESS);
        }
    }
}
