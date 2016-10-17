module ProjectsHelper
  def school_year_options
    years = []
    for i in 1..4
      years << ["#{i}e jaar", i]
    end
    years
  end
end
