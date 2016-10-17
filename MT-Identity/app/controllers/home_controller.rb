class HomeController < ApplicationController
  def index
    @college_years = CollegeYear.find(:all, :order => "start_year desc")
    @active_projects = Project.find(:all, :order => "id desc", :limit => 9).reverse
    @projects = Project.find(:all, :order => "id desc")
  end

  def about
  end

  def contact
  end
end
