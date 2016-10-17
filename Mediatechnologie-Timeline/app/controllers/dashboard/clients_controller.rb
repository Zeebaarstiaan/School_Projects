class Dashboard::ClientsController < ApplicationController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :check_admin

  def index
    @clients = Client.all

    respond_to do |format|
      format.html
      format.json { render json: @clients }
    end
  end

  def show
    @client = Client.find(params[:id])

    respond_to do |format|
      format.html
      format.json { render json: @client }
    end
  end

  def new
    @client = Client.new

    respond_to do |format|
      format.html
      format.json { render json: @client }
    end
  end

  def edit
    @client = Client.find(params[:id])
  end

  def create
    @client = Client.new(params[:client])

    respond_to do |format|
      if @client.save
        format.html { redirect_to dashboard_clients_path, notice: 'Opdrachtgever succesvol aangemaakt.' }
        format.json { render json: @client, status: :created, location: @client }
      else
        format.html { render action: "new" }
        format.json { render json: @client.errors, status: :unprocessable_entity }
      end
    end
  end

  def update
    @client = Client.find(params[:id])
    respond_to do |format|
      if @client.update_attributes(params[:client])
        format.html { redirect_to dashboard_client_path(@client), notice: 'Opdrachtgever succesvol bijgewerkt.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @client.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @client = Client.find(params[:id])
    @client.destroy

    respond_to do |format|
      format.html { redirect_to dashboard_clients_path }
      format.json { head :no_content }
    end
  end
end

private
  def check_admin
    unless current_user.is_admin?
      redirect_to dashboard_dashboard_path, alert: 'U heeft hier niet de rechten voor.'
    end
  end
