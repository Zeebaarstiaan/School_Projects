#include <SoftwareSerial.h>
#include <ParallaxRFID.h>
ParallaxRFID RFIDclient(8,6);

const int  buttonPin = 2;    // the pin that the pushbutton is attached to


int player1score = 0;
int player2score = 0;
int buttonState = 0;         // current state of the button
int lastButtonState = 0;     // previous state of the button
int x;
int current_player = 0;
int prev_player = 0;
int lastX = 0;
long previousMillis = 0;        // will store last time LED was updated
long interval = 1000;           // interval at which to blink (milliseconds)

void setup() {
  pinMode(buttonPin, INPUT);
  Serial.begin(9600);
}

void loop() {
  x=RFIDclient.readRFID(4);
  if (current_player != prev_player){
    Serial.print("A");
    Serial.print(current_player);
    Serial.println("B");
  }
  prev_player = current_player;
  if (x != 0){
    switch (x) {
      case 1:
//        Serial.println("player 1");
        current_player = 1;
        break;
      case 2:
//        Serial.println("player 2");
        current_player = 2;
        break;
    }
  }
  
  buttonState = digitalRead(buttonPin);  
  if (buttonState != lastButtonState) {
    if (buttonState == HIGH) {
      if (current_player != 0){
        if (current_player == 1){
          player1score++;
          Serial.print("Z");
          Serial.print(player1score);
          Serial.println("X");
        } else {
          player2score++;
          Serial.print("Z");
          Serial.print(player2score);
          Serial.println("X");
        }
      }
    }
   }
   lastButtonState = buttonState;
}

