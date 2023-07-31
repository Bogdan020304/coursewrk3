package Client;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.net.URL;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//Instance of the game
public class Game extends JPanel{

    int crx,cry;	//location of the crossing
    int car_x,car_y;    //x and y location of user's car
    int speedX,speedY;	//the movement values of the user's car
    int nOpponent;      //the number of opponent vehicles in the game
    String imageLoc[]; //array used to store opponent car images
    int lx[],ly[];  //integer arrays used to store the x and y values of the oncoming vehicles
    int score;      //intger variable used to store the current score of the player
    int highScore;  //integer variable used to store the high score of the player
    int speedOpponent[]; //integer array used to store the spped value of each opponent vehicle in the game
    boolean isFinished; //boolean that will be used the end the game when a colision occurs

    public Game(){
        crx = cry = 999;   //initialing setting the location of the crossing to (-999,-999)
        //Listener to get input from user when a key is pressed and released
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) { //when a key is released
                stopCar(e);
            }
            public void keyPressed(KeyEvent e) { //when a key is pressed
                moveCar(e);
            }
        });
        setFocusable(true);
        car_x = 215;
        car_y = 300;
        speedX = speedY = 0;
        nOpponent = 0;
        lx = new int[20];
        ly = new int[20];
        imageLoc = new String[20];
        speedOpponent = new int[20];
        isFinished = false;
        score = highScore = 0;
    }


    public void paint(Graphics g){
        super.paint(g);
        Graphics2D obj = (Graphics2D) g;
        obj.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try{
            obj.drawImage(getToolkit().getImage("src/Images/st_road.png"), 0, 0 ,this);
            if(cry >= -499 && crx >= -499) //if a road crossing has passed the window view
                obj.drawImage(getToolkit().getImage("src/Images/cross_road.png"),crx,cry,this);

            obj.drawImage(getToolkit().getImage("src/Images/car_self.png"),car_x,car_y,this);

            int closei = 0;
            if(this.nOpponent > 0){
                for(int i=0;i<this.nOpponent;i++){
                    closei = (i+1)%4;
                    if (closei%4==0)closei =4;
                    obj.drawImage(getToolkit().getImage("src/Images/car_"+closei+".png"),this.lx[i],this.ly[i],this);
                }
            }
            if(isFinished){
                obj.drawImage(getToolkit().getImage("src/Images/boom.png"),car_x-70,car_y-70,this);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


    void moveRoad(int count){
        if(crx == 999 && cry == 999){
            if(count%10 == 0){
                crx = 0;
                cry = -499;
            }
        }
        else{
            cry++;
        }
        if(crx == 0 && cry == 499){
            crx = cry = 999;
        }
        car_x += speedX;
        car_y += speedY;


        if(car_y < 0)
            car_y = 0;


        if(car_y >= 500-130)
            car_y = 500-130;


        if(car_x <= 115)
            car_x = 115;

        if(car_x >= 365-50)
            car_x = 365-50;


        for(int i=0;i<this.nOpponent;i++){
            this.ly[i] += speedOpponent[i];
        }

        //next 16 lines unknown
        int index[] = new int[nOpponent];
        for(int i=0;i<nOpponent;i++){
            if(lx[i] >= 127){
                index[i] = 1;
            }
        }
        int c = 0;
        for(int i=0;i<nOpponent;i++){
            if(index[i] == 1){
                imageLoc[c] = imageLoc[i];
                lx[c] = lx[i];
                ly[c] = ly[i];
                speedOpponent[c] = speedOpponent[i];
                c++;
            }
        }

        score += nOpponent - c;

        if(score > highScore)
            highScore = score;

        nOpponent = c;

        //Check for collision
        for(int i=0;i<nOpponent;i++){
            if((lx[i] >= car_x && lx[i] <= car_x+46) || (lx[i]+46 >= car_x && lx[i]+46 <= car_x+46)){
                if(car_y+87 >= ly[i] && !(car_y >= ly[i]+87)){
                    this.finish();
                }
            }
        }
    }


    void finish(){
        String str = "";
        isFinished = true;
        this.repaint();
        if(score == highScore && score != 0)
            str = "\nCongratulations!!! Its a high score";
        JOptionPane.showMessageDialog(this,"Game Over!!!\nYour Score : "+score+"\nHigh Score : "+highScore+str,     "Game Over", JOptionPane.YES_NO_OPTION);
        System.exit(ABORT);

    }



    public void moveCar(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_UP){
            speedY = -1;
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            speedY = 2;
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            speedX = 1;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            speedX = -1;
        }
    }


    public void stopCar(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_UP){
            speedY = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            speedY = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            speedX = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            speedX = 0;
        }
    }


    public static void main(String args[]){
        JFrame frame = new JFrame("Car Racing Game");
        Game game = new Game();
        frame.add(game);
        frame.setSize(500,500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int count = 1;
        while(true){
            game.moveRoad(count);
            game.repaint();
            try{
                Thread.sleep(5);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            count++;
            if(count % 200 == 0){

                System.out.println(game.nOpponent);
                System.out.println(count);

                game.ly[game.nOpponent] = -130;
                if(game.ly[game.nOpponent]<500){
                    continue;
                }
                int p = (int)(Math.random()*100)%4;
                if(p == 0){
                    p = 250;
                }
                else if(p == 1){
                    p = 300;
                }
                else if(p == 2){
                    p = 185;
                }
                else{
                    p = 130;
                }
                game.lx[game.nOpponent] = p;
                game.speedOpponent[game.nOpponent] = (int)(Math.random()*100)%2 + 2;
                game.nOpponent++;

            }

        }
    }
}

