import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable {
    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    private final int WIDTH = 240;
    private final int HEIGHT = 160;
    private final int SCALE = 3;
    private BufferedImage image;
    private Spritesheet spritesheet;
    private BufferedImage[] player;
    private int frames = 0, maxFrames = 10, curAnimation = 0, maxAnimation = 3;

    public Game() {
        spritesheet = new Spritesheet("/spritesheet.png");
        player = new BufferedImage[4];
        player[0] = spritesheet.getSprite(0,0,16,16);
        player[1] = spritesheet.getSprite(16,0,16,16);
        player[2] = spritesheet.getSprite(32,0,16,16);
        player[3] = spritesheet.getSprite(48,0,16,16);
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        this.initFrame();
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    public void initFrame() {
        frame = new JFrame("Game #1");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void tick() {
        frames++;
        if (frames > maxFrames) {
            frames = 0;
            curAnimation++;
            if (curAnimation > maxAnimation) {
                curAnimation = 0;
            }
        }
    }

    public void render() {
        BufferStrategy bufferStrategy = this.getBufferStrategy();
        if (bufferStrategy == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(85,171,85));
        graphics.fillRect(0,0,WIDTH,HEIGHT);
        /* * Renderização do jogo * */
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics.drawImage(player[curAnimation], 90, 90, null);
        /* * * * * * */
        graphics.dispose();
        graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
        bufferStrategy.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }
}
