int weiche = 12; // hier wird die Polarität des Schalters gewechselt
int dcmotor = 9; // Teil des Dc Motor Programms
int power = 3; // Strom für die Weiche
int button =A0; //input Pin für den Knopf auf dem Arduino
int Green=6; // Grüne LED wird von Digitalem Pin 6 Gesteuert
int Red=7; // Rote LED wird von Digitalem Pin 6 Gesteuert


void setup() {
  pinMode(weiche, OUTPUT); // setzen den PIN 12 als OUTPUT
  pinMode(dcmotor, OUTPUT); // setzen den PIN 9 als OUTPUT
  pinMode(power, OUTPUT); // setzen den PIN 3 als OUTPUT
  pinMode(button, INPUT); // PIN A0 als INPUT
  pinMode(Green, OUTPUT); // PIN 6 als Output
  pinMode(Red, OUTPUT); // PIN 7 als Output
}

// Enum mit den Zuständen 
enum SWITCHSTATES
{
  SS_AUS1, // SwitchState aus 1
  SS_AUS2, // SwitchState aus 2
  SS_GERADEAUS, // SwitchState GERADEAUS
  SS_KURVE, // SwitchState KURVE
};

SWITCHSTATES switchState=SS_AUS1; // Standard auf SS_AUS1 setzen

void loop(){
  int knopf_auslesen = analogRead(button); // Der Input A0 für den Knopf wird ausgelesen
  delay(100); //eine Wartezeit von 0.1 Sekunde
// Switch case zum Durchschalten des Enums
  switch(switchState){
    case SS_AUS1:
    ausschalten1(knopf_auslesen);
    break;
    case SS_AUS2:
    ausschalten2(knopf_auslesen);
    break;
    case SS_GERADEAUS:
    geradeaus(knopf_auslesen);
    break;
    case SS_KURVE:
    kurve(knopf_auslesen);
    break;
  }
}
//Ist der Grundzustand. Wenn hier der Knopf betätigt wird, wird der Kurvenzustand aufgerufen
void ausschalten1(int knopf_auslesen){
  digitalWrite(weiche,LOW); // Polarität
  digitalWrite(dcmotor,LOW); // Motor Freigeben
  digitalWrite(power,0); // Strom auf 0 setzen
  digitalWrite(Green,LOW); // Schaltet Grüne LED an
  digitalWrite(Red, HIGH); //Schaltet Rote LED an
  // High und Low getauscht wegen gemeinsamen + und gesteuertem -
  if (knopf_auslesen>500){
    switchState=SS_KURVE; //wenn der Knopf gedrückt wird Schalte die Weiche auf KURVE
  }
}
//Schaltet die Weiche auf Kurve und geht in den zweiten Auszustand
void kurve(int knopf_auslesen){
  digitalWrite(weiche,HIGH); // Polarität
  digitalWrite(dcmotor,LOW); // Motor Freigeben
  digitalWrite(power,255); // Strom auf Maximum setzen
  digitalWrite(Green, HIGH); // Schaltet grüne LED aus
  digitalWrite(Red, HIGH);//Schaltet Rote LED an
  delay(200);
  digitalWrite(weiche,HIGH); // Polarität
  digitalWrite(dcmotor,LOW); // Motor Freigeben
  digitalWrite(power,0); // Strom auf Minimum setzen
  digitalWrite(Green, HIGH); // Schaltet grüne LED aus
  digitalWrite(Red, HIGH);//Schaltet Rote LED aus
  switchState=SS_AUS2;
}

//Ist der zweite Grundzustand. Wenn hier der Knopf betätigt wird, wird der Geradeauszustand aufgerufen
void ausschalten2(int knopf_auslesen){
  digitalWrite(weiche,HIGH); // Polarität
  digitalWrite(dcmotor,LOW); // Motor Freigeben
  digitalWrite(power,0); // Strom auf 0 setzen
  digitalWrite(Green,HIGH); // Schaltet Grüne LED aus
  digitalWrite(Red, LOW); //Schaltet Rote LED an
  if (knopf_auslesen>500){
    switchState=SS_GERADEAUS; //wenn der Knopf gedrückt wird Schalte die Weiche auf KURVE
  }
}

//Schaltet die Weiche auf geradeaus und geht in den zweiten Auszustand
void geradeaus(int knopf_auslesen){
  digitalWrite(weiche,LOW); // Polarität
  digitalWrite(dcmotor,LOW); // Motor Freigeben
  digitalWrite(power,255); // Strom auf Maximum setzen
  digitalWrite(Green, HIGH); // Schaltet grüne LED aus
  digitalWrite(Red, HIGH);//Schaltet Rote LED aus
  delay(200);
  digitalWrite(weiche,LOW); // Polarität
  digitalWrite(dcmotor,LOW); // Motor Freigeben
  digitalWrite(power,0); // Strom auf Minimum setzen
  digitalWrite(Green, HIGH); // Schaltet grüne LED aus
  digitalWrite(Red, HIGH);//Schaltet Rote LED aus

  switchState=SS_AUS1;
}
