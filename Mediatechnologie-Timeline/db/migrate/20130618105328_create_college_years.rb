class CreateCollegeYears < ActiveRecord::Migration
  def change
    create_table :college_years do |t|
      t.integer :start_year, :limit => 4

      t.timestamps
    end
  end
end
