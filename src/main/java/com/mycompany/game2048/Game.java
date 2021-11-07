package com.mycompany.game2048;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;


public class Game implements Runnable  {
    
    private int width;
    private int height;
    private int xsize;
    private int ysize;
    private String title;
    private JFrame game;
    private final int tilesize=70;
    private final int border=10;
    private boolean playing;
    private long[][] area;
    private Graphics g;
    private BufferStrategy bs;
    private Canvas canvas;
    private Keyboard keyboard;
    

   private Thread thread;    
    public Game(int xsize, int ysize) {
        this.width = xsize*tilesize+(xsize+1)*border;
        this.height = ysize*tilesize+(ysize+1)*border;
        this.xsize = xsize;
        this.ysize = ysize;
        this.title = "2048 GUI Game";
        area = new long[ysize][xsize];
        keyboard = new Keyboard();
        
        
        
    }
    
    
    public void init()
    {

        
        keyboard.tick();
        
        game = new JFrame();
        game.setSize(width,height);
        game.setTitle(title);

        

        game.getContentPane().setBackground(Color.lightGray);
        game.setLocationRelativeTo(null);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setResizable(false);
        game.setVisible(true);
        
         
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setFocusable(false);
        game.add(canvas);
        game.pack();
        
        game.addKeyListener(keyboard);
        
                cleararea();
        generate();
        //g.clearRect(0, 0, width, height);
        //g.drawRect(0, 0, width, height);

        //nextround();
    }
     
    
   public synchronized void start(){
       if (playing) return;
       playing = true;
       thread = new Thread(this);
       thread.start();
   }
   
   public synchronized void stop(){
       if(!playing) return;
       playing = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
   }   
    
    
    
    
    @Override
    public void run(){
       init();
       
       int fps=60;
       double timePerTick = 1000000000 /fps;
       double delta = 0;
       long now;
       long lastTime = System.nanoTime();
       long timer = 0;
       int ticks=0;
       
       
       while(playing){
           now = System.nanoTime();
           delta += (now-lastTime) /timePerTick;
           timer += now - lastTime;
           lastTime = now;
           
           
           if(delta>= 1){
                nextround();  
                delta--;
                ticks++;
           }
           if(timer>=1000000000){
               //System.out.println(ticks);
               ticks=0;
               timer=0;
           }

       }
       stop();
       finished();
   } 
    
    
    
    
    private void cleararea()
    {
        for (int i=0;i<ysize;i++)
            for(int j=0;j<xsize;j++)
                area[i][j]=0;
        
        
    }
    
    
    private void generate()
    {
        Random rand = new Random();
        
        if (rand.nextFloat()>0.3)
            createnumber(2);
        else
            createnumber(4);
    }
    
    
    private void createnumber(int value)
    {
        Random rand = new Random();
        int a = rand.nextInt(ysize);
        int b = rand.nextInt(xsize);
        if(area[a][b] ==0 )
        {
            area[a][b] =value;

        }
        else
        {
            if(hasfreespaces())
              createnumber(value);  
            else
                playing=false;
            
        }        
    }
    
    private boolean hasfreespaces()
    {
        for (int i=0;i<ysize;i++)
            for(int j=0;j<xsize;j++)
                if(area[i][j]==0)
                    return true;
        
        return false;

        
    }
    
    
    private void nextround()
    {
        render(g);
        moveTiles();
        

        
    }
    
    private void render(Graphics g)
    {
        bs = getCanvas().getBufferStrategy();
        if(bs == null){
            getCanvas().createBufferStrategy(3);
            return;
        }
        
        g = bs.getDrawGraphics();
        //Clear Screen
        g.clearRect(0, 0, width, height);
        //Drawing
        
        for (int i=0;i<ysize;i++)
            for(int j=0;j<xsize;j++)
            {
                if(area[i][j]==0)
                {
                   g.setColor(Color.DARK_GRAY);
                   g.fillRect(border*(j+1)+tilesize*j, border*(i+1)+tilesize*i, tilesize, tilesize);
                }
                else if (area[i][j]!=0)
                {
                   g.setColor(Color.YELLOW);
                   g.fillRect(border*(j+1)+tilesize*j, border*(i+1)+tilesize*i, tilesize, tilesize);
                   g.setColor(Color.BLACK);
                   g.setFont(new Font("TimesRoman", Font.PLAIN, 40)); 
                   g.drawString(Integer.toString((int)area[i][j]), border*(j+1)+tilesize*j+tilesize/3, border*(i+1)+tilesize*i+(tilesize+30)/2);
                }

                   
            }
        
        //END

        bs.show();
        g.dispose();
        
            
    }
    
   public Keyboard getKeyboard(){
       return keyboard;
   }
    
    

    private void finished()
    {
        System.out.println("Game finished");

            
    }

  public Canvas getCanvas() {
        return canvas;
    }

  


  
    private void moveTiles(){     
     
        
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }  

            
            
        if(keyboard.keys[KeyEvent.VK_W])
        {

            
        for(int j=0;j<xsize;j++)
        {
            for (int i=0;i<ysize;i++)
            {
                if(area[i][j]==0)
                    continue;
                for(int k=i+1;k<ysize;k++)
                {
                    if(area[i][j]!=area[k][j] && area[k][j]!=0)
                        break;   
                    else if(area[i][j]==area[k][j])
                    {
                        area[i][j]+=area[i][j];
                        area[k][j]=0;
                        break;
                    }

                }
            }
            
            
            for(int i=0;i<ysize;i++)
            {
                if(area[i][j]==0)  
                for(int k=i+1;k<ysize;k++)
                {
                    if(area[k][j]!=0)
                    {
                        area[i][j]=area[k][j];
                        area[k][j]=0;
                        break;
                    }
                        
                }
            }

            
        
        }
        
                    
            generate();
        }
        else if(keyboard.keys[KeyEvent.VK_S])
        {
        
            
        for(int j=0;j<xsize;j++)
        {
            for (int i=ysize-1;i>=0;i--)
            {
                if(area[i][j]==0)
                    continue;
                for(int k=i-1;k>=0;k--)
                {   
                    if(area[i][j]!=area[k][j] && area[k][j]!=0)
                        break;   
                    else if(area[i][j]==area[k][j])
                    {
                        area[i][j]+=area[i][j];
                        area[k][j]=0;
                        break;
                    }
                    
                }
            }
        
            
            
            for(int i=ysize-1;i>=0;i--)
            {
                if(area[i][j]==0)  
                for(int k=i-1;k>=0;k--)
                {
                    if(area[k][j]!=0)
                    {
                        area[i][j]=area[k][j];
                        area[k][j]=0;
                        break;
                    }
                        
                }
            }
        
        }
            
            generate();
        }
        else if(keyboard.keys[KeyEvent.VK_A])
        {
            
            
            
        for (int i=0;i<ysize;i++)
        {
            for(int j=0;j<xsize;j++)
            {
                if(area[i][j]==0)
                    continue;
                for(int k=j+1;k<xsize;k++)
                {
                    if(area[i][j]!=area[i][k] && area[i][k]!=0)
                        break;   
                    else if(area[i][j]==area[i][k])
                    {
                        area[i][j]+=area[i][j];
                        area[i][k]=0;
                        break;
                    }

                }
            }
      
            for(int j=0;j<xsize;j++)
            {
                if(area[i][j]==0)  
                for(int k=j+1;k<xsize;k++)
                {
                    if(area[i][k]!=0)
                    {
                        area[i][j]=area[i][k];
                        area[i][k]=0;
                        break;
                    }
                        
                }
            }

                        
            
            
        }


            
            
            generate();

        }
        else if(keyboard.keys[KeyEvent.VK_D])
        {
            
            
        for (int i=0;i<ysize;i++)
        {
            for(int j=xsize-1;j>=0;j--)
            {
                if(area[i][j]==0)
                    continue;
                for(int k=j-1;k>=0;k--)
                {
                    if(area[i][j]!=area[i][k] && area[i][k]!=0)
                        break;   
                    else if(area[i][j]==area[i][k])
                    {
                        area[i][j]+=area[i][j];
                        area[i][k]=0;
                        break;
                    } 
                }
            }
            
            for(int j=xsize-1;j>=0;j--)
            {
                if(area[i][j]==0)  
                    for(int k=j-1;k>=0;k--)
                    {
                    if(area[i][k]!=0)
                    {
                        area[i][j]=area[i][k];
                        area[i][k]=0;
                        break;
                    }
                        
                    }
            }
       
        }
            
            generate();
        }
    }
    
    
    
    
}
