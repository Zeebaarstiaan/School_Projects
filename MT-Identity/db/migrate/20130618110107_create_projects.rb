class CreateProjects < ActiveRecord::Migration
  def change
    create_table :projects do |t|
      t.string :title
      t.text :description
      t.integer :school_year
      t.string :preview
      t.boolean :highlight, :default => false

      t.timestamps
    end

    create_table :college_years_projects, :id => false do |t|
      t.references :college_year, :project
    end

    add_index :college_years_projects, [:college_year_id, :project_id]

  end
end
