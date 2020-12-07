package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    //declare variables
    TextView txtWordToBeGuessed;
    String wordToBeGuessed;
    String wordDisplayedString;
    char[] wordDisplayCharArray;
    ArrayList<String> myListOfWords;
    EditText edtInput;
    TextView txtLettersTried;
    String lettersTried;
    final String MESSAGE_WITH_LETTERS_TRIED = "Letters Tried: ";
    TextView txtTriesLeft;
    String triesLeft;
    final String WINNING_MESSAGE = "You Won!";
    final String LOSING_MESSAGE = "You Lost!";
    Animation rotateAnimation;
    Animation scaleAnimation;
    Animation scaleAndRotateAnimation;

    void revealLetterInWord(char letter){
        int indexOfLetter = wordToBeGuessed.indexOf(letter);

        while(indexOfLetter >=0){
            wordDisplayCharArray[indexOfLetter] = wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = wordToBeGuessed.indexOf(letter, indexOfLetter + 1);
        }

        wordDisplayedString = String.valueOf(wordDisplayCharArray);

    }

    void displayWordOnScreen() {
        String formattedString = "";
        for(char character : wordDisplayCharArray){
            formattedString += character + " ";
        }
        txtWordToBeGuessed.setText(formattedString);

    }


    void initializeGame() {
        Collections.shuffle(myListOfWords);
        wordToBeGuessed = myListOfWords.get(0);
        myListOfWords.remove(0);


        wordDisplayCharArray = wordToBeGuessed.toCharArray();


        for(int i = 1; i < wordDisplayCharArray.length; i++) {
            wordDisplayCharArray[i] = '_';
        }

        revealLetterInWord(wordDisplayCharArray[0]);

        revealLetterInWord(wordDisplayCharArray[wordDisplayCharArray.length - 1]);

        wordDisplayedString = String.valueOf(wordDisplayCharArray);

        displayWordOnScreen();

        edtInput.setText("");

        lettersTried = " ";

        txtLettersTried.setText(MESSAGE_WITH_LETTERS_TRIED);

        triesLeft = " X X X X X";
        txtTriesLeft.setText(triesLeft);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init variables
        myListOfWords = new ArrayList<String>();
        txtWordToBeGuessed = findViewById(R.id.txtWordToBeGuessed);
        edtInput = (EditText) findViewById(R.id.edtInput);
        txtLettersTried = (TextView) findViewById(R.id.txtLettersTried);
        txtTriesLeft = (TextView) findViewById(R.id.txtTriesLeft);
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);


        //traverse database file and populate array
        InputStream myInputStream = null;
        Scanner in = null;
        String aWord = "";


            try {
                myInputStream = getAssets().open("database_file.txt");
                in = new Scanner(myInputStream);
                while(in.hasNext()) {
                    aWord = in.next();
                    myListOfWords.add(aWord);

                }


            } catch (IOException e) {
                Toast.makeText(MainActivity.this,
                        e.getClass().getSimpleName() + ": " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
            finally {
                if (in != null) {
                    in.close();
                }
                //close InputStream
                try {
                    if(myInputStream != null) {
                        myInputStream.close();
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this,
                            e.getClass().getSimpleName() + ": " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            initializeGame();

            edtInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(s.length() != 0) {
                        checkIfLetterIsInWord(s.charAt(0));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        void checkIfLetterIsInWord(char letter){
        if(wordToBeGuessed.indexOf(letter) >= 0){

            if(wordDisplayedString.indexOf(letter) < 0){

                txtWordToBeGuessed.startAnimation(scaleAnimation);

                revealLetterInWord(letter);

                displayWordOnScreen();

                if(!wordDisplayedString.contains("_")){
                    txtTriesLeft.setText(WINNING_MESSAGE);
                }
              }
           }
        else{

            decreaseAndDisplayTriesLeft();

            if(triesLeft.isEmpty()) {
                txtTriesLeft.setText(LOSING_MESSAGE);
                txtWordToBeGuessed.setText(wordToBeGuessed);
               }
            }
        if(lettersTried.indexOf(letter) < 0){
            lettersTried += letter + " , ";
            String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
            txtLettersTried.setText(messageToBeDisplayed);


           }
        }

        void decreaseAndDisplayTriesLeft(){

        if(!triesLeft.isEmpty()){

            txtTriesLeft.startAnimation(scaleAnimation);


            triesLeft = triesLeft.substring(0, triesLeft.length() -2 );
            txtTriesLeft.setText(triesLeft);


           }
        }

        void resetGame(View v){

        v.startAnimation(rotateAnimation);


        initializeGame();


        }
    }