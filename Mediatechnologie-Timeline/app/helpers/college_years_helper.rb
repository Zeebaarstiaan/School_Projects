module CollegeYearsHelper

  def cohort_select
    years = []
    for i in (Date.new(2005,1,1).year)..(Date.today.year+3)
      years << ["#{i} - #{i+1}", i]
    end
    years
  end

end
