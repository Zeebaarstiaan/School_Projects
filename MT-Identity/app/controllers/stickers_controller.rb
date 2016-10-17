class StickersController < ApplicationController
  before_filter :authenticate_user!

  def index
    @stickers = Sticker.all
    @card = Card.find(params[:card_id])
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @stickers }
    end
  end

  def show
    @sticker = Sticker.find(params[:id])
    @card = Card.find(params[:card_id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @sticker }
    end
  end

  def new
    @card = Card.find(params[:card_id])

    unless @card.sticker.present?
      @sticker = Sticker.new
      @sticker.title = "#{@card.title} Sticker"
      respond_to do |format|
        format.html # new.html.erb
        format.json { render json: @sticker }
      end
    else
      redirect_to card_path(@card), alert: 'Sticker already exists.'
    end
  end


  def edit
    @sticker = Sticker.find(params[:id])
    @card = Card.find(params[:card_id])

  end

  def create
    @sticker = Sticker.new(params[:sticker])

    respond_to do |format|
      if @sticker.save
        format.html { redirect_to card_sticker_path(@sticker.card, @sticker), notice: 'Sticker is succesvol aangemaakt.' }
        format.json { render json: @sticker, status: :created, location: @sticker }
      else
        format.html { render action: "new" }
        format.json { render json: @sticker.errors, status: :unprocessable_entity }
      end
    end
  end

  def update
    @sticker = Sticker.find(params[:id])

    respond_to do |format|
      if @sticker.update_attributes(params[:sticker])
        format.html { redirect_to @sticker, notice: 'Sticker is succesvol bijgewerkt.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @sticker.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @sticker = Sticker.find(params[:id])
    @card = Card.find(params[:card_id])

    @sticker.destroy

    respond_to do |format|
      format.html { redirect_to card_stickers_path(@card) }
      format.json { head :no_content }
    end
  end
end
