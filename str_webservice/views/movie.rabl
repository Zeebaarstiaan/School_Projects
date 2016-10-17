object @movie => :movie
attributes :id, :title, :description, :released, :created_at, :updated_at
child :reviews => :reviews do
  attribute :id, :rating, :comment, :created_at, :updated_at
end