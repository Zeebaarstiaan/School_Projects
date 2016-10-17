class Dashboard::ProjectsController < ApplicationController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :check_admin

  def index
    @college_years = CollegeYear.find(:all, :order => "start_year asc")
    @projects = Project.all

    respond_to do |format|
      format.html
      format.json { render json: @projects }
    end
  end

   def show
    @project = Project.find(params[:id])

    respond_to do |format|
      format.html
      format.json { render json: @project }
    end
  end

  def new
    @project = Project.new
    @college_years = CollegeYear.all
    @clients = Client.all
    respond_to do |format|
      format.html
      format.json { render json: @project }
    end
  end

  def edit
    @project = Project.find(params[:id])
    @college_years = CollegeYear.all
    @clients = Client.all
  end

  def create
    @project = Project.new(params[:project])

    respond_to do |format|
      if @project.save
        format.html { redirect_to dashboard_project_path(@project), notice: 'Project is succesvol aangemaakt.' }
        format.json { render json: @project, status: :created, location: @project }
      else
        format.html { render action: "new" }
        format.json { render json: @project.errors, status: :unprocessable_entity }
      end
    end
  end

  def update
    @project = Project.find(params[:id])

    respond_to do |format|
      if @project.update_attributes(params[:project])
        format.html { redirect_to dashboard_project_path(@project), notice: 'Project is succesvol aangemaakt.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @project.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @project = Project.find(params[:id])
    @project.destroy
    @project.groups.destroy_all

    respond_to do |format|
      format.html { redirect_to dashboard_projects_url }
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
