class CollegeYearsController < ApplicationController

  def show
    @college_year = CollegeYear.find(params[:id])
    @college_years = CollegeYear.find(:all, :order => "start_year asc")
    current_index = @college_years.index(@college_year)
    @next_college_year = @college_years[current_index+1]
    if current_index == 0
      @previous_college_year = nil
    else
      @previous_college_year = @college_years[current_index-1]
    end

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @college_year }
    end
  end

end
