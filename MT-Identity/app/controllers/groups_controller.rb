class GroupsController < ApplicationController
  def show
    @college_year = CollegeYear.find(params[:college_year_id])
    @project = Project.find(params[:project_id])
    @group = Group.find(params[:id])

    respond_to do |format|
      format.html
      format.js
    end
  end
end
