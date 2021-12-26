//Quelle:https://drive.google.com/file/d/1038dZtfMV2y0Y41BSIceeRDO31unvA_s/view wurde als Vorlage benutzt
const int switch1pin1=2; //Motor Driver ist über Pins 2-5 Verbunden. Pin 2 und 3 kontrollieren 1. Weiche
const int switch1pin2=3;
const int switch2pin1=4; // Pins 4 und 5 kontrollieren 2. Weiche
const int switch2pin2=5;
// weiterer Motor Driver an Pins 6-9 anschließen 
//const int switch3pin1=6; Für Weiche 3 Pin 6 und 7
//const int switch3pin2=7;
//const int switch4pin1=8; Für Weiche 4 Pin 8 und 9
//const int switch4pin2=9;

const int button1 = A0; // Knopf 1 ist an A0 Angeschlossen und wird Weiche 1 schalten
const int button2 = A1; // Knopf 2 ist an A1 Angeschlossen und wird Weiche 2 schalten
// Für weitere Weichen werden Knöpfe an A2-A5 angeschlossen
// const int button3 = A2; // Knopf 2 ist an A1 Angeschlossen und wird Weiche 1 schalten

int state1=0; // setzt den Aktuellen Status von Knopf1
int state2=0; // setzt den Aktuellen Status von Knopf2
void setup() {
  // Ausgabe Pins werden Festgelegt und der Serielle Monitor gestartet
Serial.begin(9600);// Setzt die Baud Rate auf 9600 und startet den Seriellen Monitor. 
pinMode(switch1pin1,OUTPUT); //legt die Digital Pins 2-5 als Ausgabe Pins fest
pinMode(switch1pin2,OUTPUT);
pinMode(switch2pin1,OUTPUT);
pinMode(switch2pin2,OUTPUT);
//usw für weitere Motor Driver
}
// Enum über die Zustände
enum SWITCHSTATES{
  SS_AUS,
  SS_GERADE1,
  SS_KURVE1,
  SS_GERADE2,
  SS_KURVE2,
  //usw für weitere Weichen
};
// Startstatus wird gesetzt
SWITCHSTATES switchState = SS_AUS;
void loop() {
 Serial.print("Status 1:");
 Serial.println(state1); //gibt den Status der ersten Weiche im Seriellen Monitor aus
 Serial.print("Status 2:");
 Serial.println(state2); //gibt den Status der zweiten Weiche im Seriellen Monitor aus
 //usw für weitere Weichen

 int button1Lesen=analogRead(button1); //Speichert den Wert von button1 ab
 int button2Lesen=analogRead(button2); //Speichert den Wert von button2 ab
 Serial.print("Button 1: ");
  Serial.println(button1Lesen);//display in serial monitor what the State of Button 1 is with the text "Button 1" in front.
  Serial.print("Button 2: ");
  Serial.println(button2Lesen);//display in serial monitor what the State of Button 2 is with the text "Button 2" in front.
  delay(200);// 0.2 second pause between readings

 //Switch Case der über den Enum Schaltet und die Funktionen für die Weichenschaltung aufruft
 switch(switchState)
 {
  case SS_AUS:
  switchAus(button1Lesen, button2Lesen);
  break;
  case SS_GERADE1:
  switchGerade1(button1Lesen, button2Lesen);
   break;
  case SS_KURVE1:
  switchKurve1(button1Lesen, button2Lesen);
   break;
  case SS_GERADE2:
  switchGerade2(button1Lesen, button2Lesen);
   break;
  case SS_KURVE2:
  switchKurve2(button1Lesen, button2Lesen);
   break;
 }
 
}
//switchAus schaltet alle OUTPUTS auf LOW und ist der Ruhezustand
void switchAus(int button1Lesen, int button2Lesen){
  digitalWrite(switch1pin1,LOW);
  digitalWrite(switch1pin2,LOW);
  digitalWrite(switch2pin1,LOW);
  digitalWrite(switch2pin2,LOW);

  if((button1Lesen>1000) && (state1==1)){
    switchState=SS_GERADE1; 
  }
  if((button1Lesen>1000) && (state1==0)){
    switchState=SS_KURVE1;
  }
  if((button2Lesen>1000) && (state2==1)){
    switchState=SS_GERADE2;
  }
  if((button2Lesen>1000) && (state2==0)){
    switchState=SS_KURVE2;
  }
  //usw für weitere Weichen
}
//switchGerade1 schaltet Weiche 1 auf Gerade indem switch1pin1 auf HIGH geschaltet wird
void switchGerade1(int button1Lesen,int button2Lesen){
  digitalWrite(switch1pin1,HIGH);
  digitalWrite(switch1pin2,LOW);
  digitalWrite(switch2pin1,LOW);
  digitalWrite(switch2pin2,LOW);
  delay(500); //Strom fließt für 0.5 Sekunden zu der Weiche
  state1=0;
  switchState=SS_AUS;
}

//switchKurve1 schaltet Weiche 1 auf Kurve indem switch1pin2 auf HIGH geschaltet wird
void switchKurve1(int button1Lesen,int button2Lesen){
  digitalWrite(switch1pin1,LOW);
  digitalWrite(switch1pin2,HIGH);
  digitalWrite(switch2pin1,LOW);
  digitalWrite(switch2pin2,LOW);
  delay(500); //Strom fließt für 0.5 Sekunden zu der Weiche
  state1=1;
  switchState=SS_AUS; //Schaltet Strom wieder aus und wechselt in den Ruhezustand
}

//switchGerade2 schaltet Weiche 2 auf Gerade indem switch2pin1 auf HIGH geschaltet wird
void switchGerade2(int button1Lesen,int button2Lesen){
  digitalWrite(switch1pin1,LOW);
  digitalWrite(switch1pin2,LOW);
  digitalWrite(switch2pin1,HIGH);
  digitalWrite(switch2pin2,LOW);
  delay(500); //Strom fließt für 0.5 Sekunden zu der Weiche
  state2=0;
  switchState=SS_AUS;
}
//switchKurve2 schaltet Weiche 2 auf Kurve indem switch2pin2 auf HIGH geschaltet wird
void switchKurve2(int button1Lesen,int button2Lesen){
  digitalWrite(switch1pin1,LOW);
  digitalWrite(switch1pin2,LOW);
  digitalWrite(switch2pin1,LOW);
  digitalWrite(switch2pin2,HIGH);
  delay(500); //Strom fließt für 0.5 Sekunden zu der Weiche
  state2=1;
  switchState=SS_AUS; //Schaltet Strom wieder aus und wechselt in den Ruhezustand
}
