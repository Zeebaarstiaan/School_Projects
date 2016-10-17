class Dashboard::Projects::GroupsController < Dashboard::GroupsController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :get_college_year_project
  skip_before_filter :get_college_year_project, :only => [:update]
  before_filter :is_authorized?, only: [:edit, :update, :destroy, :destroy_pictures]

  def index
    @groups = @project.groups.where(:college_year_id => @college_year.id)

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @groups }
    end
  end

  def show
    @group = Group.find(params[:id])
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @group }
    end
  end

  def new
    @group = Group.new
    @group.project_id = @project.id
    @group.college_year_id = @college_year.id

    @users = User.all
    @college_years = CollegeYear.all
    @clients = Client.all

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @group }
    end
  end

  def edit
    @group = Group.find(params[:id])

    @users = User.all
    @college_years = CollegeYear.all
    @clients = Client.all

    @picture = @group.pictures.build
    @pictures = @group.pictures
  end

  def create
    @users = User.all
    @college_years = CollegeYear.all
    @clients = Client.all

    if params[:group][:college_year_id].include?('-')
      params[:group][:college_year_id] = CollegeYear.find_by_start_year(params[:group][:college_year_id][0,4]).id
    end
    @group = Group.new(params[:group])

    respond_to do |format|
      if @group.save
        # unless @group.users.include?(current_user) && !current_user.admin?
        #   @group.users << current_user
        # end
        format.html { redirect_to edit_dashboard_college_year_project_group_path(@college_year, @project, @group), notice: 'Team was succesvol aangemaakt.' }
        format.json { render json: @group, status: :created, location: @group }
      else
        format.html { render action: "new" }
        format.json { render json: @group.errors, status: :unprocessable_entity }
      end
    end
  end

  def update
    @users = User.all
    @college_years = CollegeYear.all
    @clients = Client.all

    @group = Group.find(params[:id])

    if params[:group][:college_year_id].include?('-')
      params[:group][:college_year_id] = CollegeYear.find_by_start_year(params[:group][:college_year_id][0,4]).id
    end
    respond_to do |format|
      if @group.update_attributes(params[:group])
        # unless @group.users.include?(current_user) && !current_user.admin?
        #   @group.users << current_user
        # end
        format.html { redirect_to edit_dashboard_college_year_project_group_path(@group.college_year_id, @group.project_id, @group), notice: 'Team was succesvol bijgewerkt.' }
        format.json { head :no_content }
      else
        format.html { render action: "new" }
        format.json { render json: @group.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @group = Group.find(params[:id])
    @group.destroy

    respond_to do |format|
      format.html { redirect_to dashboard_groups_url }
      format.json { head :no_content }
    end
  end

  def destroy_pictures
    @group = Group.find(params[:id])
    @group.pictures.destroy_all

    respond_to do |format|
      format.html { redirect_to dashboard_project_cohort_groups_url(@project, @cohort)}
      format.js
    end
  end

end

private
  def get_college_year_project
    @college_year = CollegeYear.find(params[:college_year_id])
    @project = Project.find(params[:project_id])
  end

  def is_authorized?
    @group = Group.find(params[:id])
    unless current_user.is_admin? || @group.users.include?(current_user)
      redirect_to dashboard_groups_path, alert: 'U heeft hier niet de rechten voor.'
    end
  end
