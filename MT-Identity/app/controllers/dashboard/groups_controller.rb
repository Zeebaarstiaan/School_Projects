class Dashboard::GroupsController < ApplicationController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :is_authorized?, only: [:edit, :update, :destroy, :destroy_pictures]

  def index
    if current_user.is_admin?
      @groups = Group.all
    else
      @groups = current_user.groups
    end

    respond_to do |format|
      format.html
      format.json { render json: @groups }
    end
  end

  def show
    @group = Group.find(params[:id])
    respond_to do |format|
      format.html
      format.json { render json: @group }
    end
  end

  def new
    @group = Group.new
    @users = User.all
    @college_years = CollegeYear.all
    @clients = Client.all

    respond_to do |format|
      format.html
      format.json { render json: @group }
    end
  end

  def edit
    @group = Group.find(params[:id])
    @projects = Project.all
    @college_years = CollegeYear.all
    @clients = Client.all
    @users = User.all
    @picture = @group.pictures.build
    @pictures = @group.pictures
  end

  def create
    @projects = Project.all
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
        format.html { redirect_to edit_dashboard_group_path(@group), notice: 'Team is succesvol aangemaakt.' }
        format.json { render json: @group, status: :created, location: @group }
      else
        format.html { render action: "new" }
        format.json { render json: @group.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /groups/1
  # PUT /groups/1.json
  def update
    @projects = Project.all
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
        format.html { redirect_to dashboard_group_path(@group), notice: 'Team is succesvol bijgewerkt.' }
        format.json { head :no_content }
      else
        format.html { render action: "new" }
        format.json { render json: @group.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /groups/1
  # DELETE /groups/1.json
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
      format.html { redirect_to dashboard_groups_url }
      format.js
    end
  end
end

private
  def is_authorized?
    @group = Group.find(params[:id])
    unless current_user.is_admin? || @group.users.include?(current_user)
      redirect_to dashboard_groups_path, alert: 'U heeft hier niet de rechten voor.'
    end
  end
