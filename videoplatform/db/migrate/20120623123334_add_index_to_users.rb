class AddIndexToUsers < ActiveRecord::Migration
  def change
  	add_index :users, :login_code, unique: true
  	add_index :users, :remember_token
  end
end
