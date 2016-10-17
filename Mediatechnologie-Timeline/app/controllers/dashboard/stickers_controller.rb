class Dashboard::StickersController < ApplicationController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :check_admin

  def index
    @projects = Project.all

    respond_to do |format|
      format.html
      format.json { render json: @projects }
    end
  end

  def show
    @sticker = Sticker.find(params[:id])
    @project = Project.find(params[:project_id])

    respond_to do |format|
      format.html
      format.json { render json: @sticker }
    end
  end

  def new
    @project = Project.find(params[:project_id])

    unless @project.sticker.present?
      @sticker = Sticker.new
      respond_to do |format|
        format.html
        format.json { render json: @sticker }
      end
    else
      redirect_to dashboard_stickers_path, alert: 'Sticker already exists.'
    end
  end


  def edit
    @sticker = Sticker.find(params[:id])
    @project = Project.find(params[:project_id])
  end

  def create
    @sticker = Sticker.new(params[:sticker])

    respond_to do |format|
      if @sticker.save
        format.html { redirect_to dashboard_project_sticker_path(@sticker.project, @sticker), notice: 'Sticker is succesvol aangemaakt.' }
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
        format.html { redirect_to dashboard_project_sticker_path(@sticker.project, @sticker), notice: 'Sticker is succesvol bijgewerkt' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @sticker.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @sticker = Sticker.find(params[:id])
    @project = Project.find(params[:project_id])

    @sticker.destroy

    respond_to do |format|
      format.html { redirect_to dashboard_stickers_path }
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
