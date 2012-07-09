package edu.harvard.med.hks.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Feedback extends AbstractTimestampEntity {
	@NotNull
	@ManyToOne
	private Game game;
	@NotNull
	@ManyToOne
	private Slot slot;
	private int speed;
	private int instruction;
	private int interesting;
	private String strategy;
	private String thoughts;

	public Game getGame() { return game; }
	public int getInstruction() { return instruction; }
	
	public int getInteresting() { return interesting; }
	
	public Slot getSlot() { return slot; }
	
	public int getSpeed() { return speed; }
	
	@Column(length=1000)
	public String getStrategy() { return strategy; }
	
	@Column(length=5000)
	public String getThoughts() { return thoughts; }
	
	public void setGame(Game game) { this.game = game; }
	
	public void setInstruction(int instruction) { this.instruction = instruction; }
	
	public void setInteresting(int interesting) { this.interesting = interesting; }
	
	public void setSlot(Slot slot) { this.slot = slot; }
	
	public void setSpeed(int speed) { this.speed = speed; }
	
	public void setStrategy(String strategy) { this.strategy = strategy; }
	
	public void setThoughts(String thoughts) { this.thoughts = thoughts; }
}