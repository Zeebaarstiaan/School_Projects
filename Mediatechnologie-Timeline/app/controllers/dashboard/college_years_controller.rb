class Dashboard::CollegeYearsController < ApplicationController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :check_admin

  def index
    @college_years = CollegeYear.find(:all, :order => "start_year desc")

    respond_to do |format|
      format.html
      format.json { render json: @college_years }
    end
  end

  def show
    @college_year = CollegeYear.find(params[:id])

    respond_to do |format|
      format.html
      format.json { render json: @college_year }
    end
  end

  def new
    @college_year = CollegeYear.new

    respond_to do |format|
      format.html
      format.json { render json: @college_year }
    end
  end

  def edit
    @college_year = CollegeYear.find(params[:id])
  end

  def create
    @college_year = CollegeYear.new(params[:college_year])

    respond_to do |format|
      if @college_year.save
        format.html { redirect_to dashboard_college_years_path, notice: 'Collegejaar succesvol aangemaakt.' }
        format.json { render json: @college_year, status: :created, location: @college_year }
      else
        format.html { render action: "new" }
        format.json { render json: @college_year.errors, status: :unprocessable_entity }
      end
    end
  end

  def update
    @college_year = CollegeYear.find(params[:id])
    respond_to do |format|
      if @college_year.update_attributes(params[:college_year])
        format.html { redirect_to dashboard_college_year_path(@college_year), notice: 'Collegejaar succesvol bijgewerkt.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @college_year.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @college_year = CollegeYear.find(params[:id])
    @college_year.destroy

    respond_to do |format|
      format.html { redirect_to dashboard_college_years_path }
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
