import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java. util.Random;
import javax.swing.*;

public class BlackJack {
    private class Betting{
        float balance;
        float atStake= 0;
        public Betting(float balance){
            this.balance = balance;
        }
        public boolean sufficientBalance(float bet){return this.balance - bet >=0;
            
        }
        public void bet(float amount){
            if (sufficientBalance(amount)){
                this.atStake = amount;
                this.balance -= amount;
            }
            else{
                System.out.println("Insufficient balance");
            }
        }
        public void winBet(){
            this.balance += 2*atStake;
            this.atStake = 0.0f;
        }
        public void tieBet(){
            this.balance += atStake;
            this.atStake = 0.0f;
        }
        public void loseBet(){
            this.atStake = 0.0f;
        }

        public float getBalance(){
            return this.balance;
        }
        public float getAtStake(){
            return this.atStake;
        }

    }
    private class Card{
        String value;
        String type;

        public Card(String value, String type){
            this.value = value;
            this.type = type;

        }
        public int getValue(){
            if ("AJQK".contains(value)){
                if (value == "A"){
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce(){
            return value == "A";
        }

        public String getImagePath(){
            return "./cards/" + toString()+".png";
        }

        @Override
        public String toString(){
            return value +"-"+ type;
        }
    }
    ArrayList<Card> deck;
    Random random = new Random(); //shuffling deck
    // deler
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;
    //player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;
    // betting functionality
    Betting betting ;

    

    //Window
    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110; //ratio 1/1,4
    int cardHeight = 154;





    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            try{
                //draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (! stayButton.isEnabled()){
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg,20,20,cardWidth,cardHeight,null);
                //draw dealers hand
                for (int i = 0; i<dealerHand.size();i++){
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg,cardWidth + 25+ (cardWidth+5)* i,20,cardWidth,cardHeight,null);
                }
                // draw player hand
                for (int i = 0; i<playerHand.size();i++){
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg,20+(cardWidth+5)*i,320,cardWidth,cardHeight,null);

                }
                
                
                if (!stayButton.isEnabled()){
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if ( playerSum > 21){
                        message = "You lose !";
                        betting.loseBet();
                        updateLabels();
                    }
                    else if (dealerSum> 21){
                        message = "You win!";
                        betting.winBet();
                        updateLabels();

                    }
                    else if ( playerSum == dealerSum){
                        message = "Tie!";
                    }
                    else if (playerSum > dealerSum){
                        message = "You win!";
                        betting.winBet();
                        updateLabels();
                    }
                    else if (playerSum < dealerSum){
                        message = "You lose ! ";
                        betting.loseBet();
                        updateLabels();
                    }
                    g.setFont(new Font("Arial",Font.PLAIN,30));
                    g.setColor(Color.white);
                    g.drawString(message,220,250);

                }

                    
            }catch (Exception e){
                    e.printStackTrace();
                }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    JButton resetButton = new JButton("Reset");
    JLabel betLabel = new JLabel("<html>Bet Amount <font color='green'>$</font> :</html>");
    JTextField betTextfield = new JTextField("      ");
    JLabel balanceLabel ;
    JLabel atStakeLabel ;

    

    


    public BlackJack(){
        betting = new Betting(100.0f);
        balanceLabel = new JLabel("<html> Balance: <font color ='green'> $" + betting.getBalance() + "</font></html>");
        atStakeLabel = new JLabel("At Stake: $"+betting.getAtStake());
        atStakeLabel.setFont(new Font("Arial",Font.BOLD,20));
        atStakeLabel.setForeground(Color.yellow);
        startGame();
        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        gamePanel.add(atStakeLabel,BorderLayout.EAST);
        frame.add(gamePanel);


        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        resetButton.setFocusable(false);
        buttonPanel.add(resetButton);
        buttonPanel.add(balanceLabel);
        //buttonPanel.add(atStakeLabel);
        buttonPanel.add(betLabel);
        buttonPanel.add(betTextfield);
        
        frame.add(buttonPanel,BorderLayout.SOUTH);
        resetButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                startGame();
                gamePanel.repaint();

            }
        });
        hitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            Card card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1:0;
            playerHand.add(card);
            if(reducePlayerAce()> 21){
                hitButton.setEnabled(false);
            }
            gamePanel.repaint();


        }});
        stayButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e ){
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while ( dealerSum < 17){
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1:0;
                    dealerHand.add(card);               
                 }
                 gamePanel.repaint();
                 updateLabels();
            }
        });
        betTextfield.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                float bet = Float.parseFloat(betTextfield.getText());
                if (betting.sufficientBalance(bet)){
                    betting.bet(bet);
                    updateLabels();
                    
                    gamePanel.repaint();
                }
                else{
                    System.out.println("Insufficient balance");
                }
            }
        });
        
        gamePanel.repaint();



    }
    // Method to update labels
    private void updateLabels() {
        balanceLabel.setText("Balance: $" + betting.getBalance());
        atStakeLabel.setText("At Stake: $" + betting.getAtStake());
    }
    public void startGame(){
        if (deck!=null){
            resetDeck();
        resetScores();
        clearHands();
        resetButtons();

        }
        
        buildDeck();
        shuffleDeck();
        //dealer
        dealerHand = new ArrayList<Card> ();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1);
        dealerSum+= hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ?  1:0;

        Card card = deck.remove(deck.size()-1);
        dealerSum +=card.getValue();
        dealerAceCount += card.isAce() ?  1:0;
        dealerHand.add(card);
        System.out.println("DEALER HAND");
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        // player 
        playerHand = new ArrayList<Card>();
        playerSum=0;

        for (int i =0; i<2; i++){
            card = deck.remove(deck.size()-1);
            playerSum+= card.getValue();
            playerAceCount += card.isAce() ? 1:0;
            playerHand.add(card);
            

        }
        System.out.println("PLAYER HAND");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);


    }

    public void buildDeck(){
        deck = new ArrayList<Card>();
        String[] values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] types = {"C","D","H","S"};

        for (int i= 0; i< types.length;i++){
            for (int j = 0; j < values.length;j++){
                Card card = new Card(values[j],types[i]);
                deck.add(card);
            }

        }
        System.out.println("BUILD DECK");
        System.out.println(deck);


    }
    public void shuffleDeck(){
        for (int i=0; i< deck.size(); i++){
            int j = random.nextInt(deck.size());
            Card currentCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i,randomCard);
            deck.set(j,currentCard);
        }
        System.out.println("After shuffle");
        System.out.println(deck);

    }
    public int reducePlayerAce(){
        while( playerSum > 21 && playerAceCount >0){
            playerSum -= 10;
            playerAceCount -=1;
        }
        return playerSum;
    }
    public int reduceDealerAce(){
        while( dealerSum > 21 && dealerAceCount >0){
            dealerSum -= 10;
            dealerAceCount -=1;
        }
        return dealerSum;

    }
    public void clearHands(){
        playerHand.clear();
        dealerHand.clear();
    }
    public void resetScores(){
        playerSum = 0;
        dealerSum = 0;
        playerAceCount=0;
        dealerAceCount = 0;

    }
    public void resetButtons(){
        stayButton.setEnabled(true);
        hitButton.setEnabled(true);
    }
    public void resetDeck(){
        shuffleDeck();
    }

}