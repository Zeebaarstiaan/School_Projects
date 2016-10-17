class AddSpatialDataToUsers < ActiveRecord::Migration
  def change
    add_column :users, :lonlat, :point, :geographic => true
    add_index :users, :lonlat, :spatial => true, using: 'GIST'
  end
end
