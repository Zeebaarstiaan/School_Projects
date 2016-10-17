class ProjectsController < ApplicationController
   def show
    @project = Project.find(params[:id])

    if params[:college_year_id].present?
      @college_year = CollegeYear.find(params[:college_year_id])
    else
      @college_year = @project.college_years.last
    end

    @groups = @project.groups.where(:college_year_id => @college_year)

    respond_to do |format|
      format.html
      format.js
    end
  end
end