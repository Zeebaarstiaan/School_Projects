class Dashboard::CollegeYears::ProjectsController < Dashboard::ProjectsController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :check_admin

  def index
    @college_year = CollegeYear.find(params[:college_year_id])
    @projects = @college_year.projects

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @projects }
    end
  end

   def show
    @college_year = CollegeYear.find(params[:college_year_id])
    @project = Project.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @project }
    end
  end

  def new
    @college_year = CollegeYear.find(params[:college_year_id])
    @project = Project.new
    @college_years = CollegeYear.all
    @clients = Client.all
    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @project }
    end
  end

  def edit
    @college_year = CollegeYear.find(params[:college_year_id])
    @project = Project.find(params[:id])
    @college_years = CollegeYear.all
    @clients = Client.all
  end

  def create
    @college_year = CollegeYear.find(params[:college_year_id])
    @project = Project.new(params[:project])

    respond_to do |format|
      if @project.save
        format.html { redirect_to dashboard_college_year_projects_path(@college_year), notice: 'Project was succesvol aangemaakt.' }
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
        format.html { redirect_to dashboard_project_path(@project), notice: 'Project was succesvol bijgewerkt.' }
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
