/**
 * Basic code that tests the colour capabilities of NinjaTerm.
 *
 * @author Geoffrey Hunter <gbmhunter@gmail.com> (www.mbedded.ninja)
 * @since 2016-09-27
 * @last-modified 2016-09-27
 */

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
}

void loop() {

#define DISPLAY_PERIOD (1000)

  Serial.println("\x1B[31mnormal red");
  delay(DISPLAY_PERIOD);
  Serial.println("\x1B[32mnormal green");
  delay(DISPLAY_PERIOD);

}



