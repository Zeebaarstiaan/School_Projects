MTIdentity::Application.routes.draw do

  # Devise users
  devise_for :users, :controllers => { :sessions => "sessions" }

  # Home routes
  root to: "home#index"
  get "/" => "home#index"
  get "/contact" => "home#contact"
  get "/about" => "home#about"
  get "/college_year_overview" => "home#college_year_overview"
  get "/project_overview" => "home#project_overview"
  get "/timeline" => "static_pages#timeline"

  # Home resources
  resources :home
  resources :users
  resources :projects

  resources :college_years do
    resources :projects do
      resources :groups
    end
  end


  # Dashboard resources
  namespace :dashboard do
    match "/" => "home#dashboard", :as => :dashboard

    # main resources
    resources :home
    resources :users
    resources :college_years
    resources :projects
    resources :groups
    resources :stickers
    resources :pictures
    resources :clients

    # nested resources
    resources :college_years do
      resources :projects, :controller => "college_years/projects" do
        resources :groups, :controller => "projects/groups"
      end
    end

    resources :projects do
      resources :groups, :controller => "projects/groups"
      resources :stickers
    end

    resources :groups do
      resources :pictures
      member do
        get 'destroy_pictures'
      end
    end
  end

  # The priority is based upon order of creation:
  # first created -> highest priority.

  # Sample of regular route:
  #   match 'products/:id' => 'catalog#view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   match 'products/:id/purchase' => 'catalog#purchase', :as => :purchase
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Sample resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Sample resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Sample resource route with more complex sub-resources
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', :on => :collection
  #     end
  #   end

  # Sample resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end

  # You can have the root of your site routed with "root"
  # just remember to delete public/index.html.
  # root :to => 'welcome#index'

  # See how all your routes lay out with "rake routes"

  # This is a legacy wild controller route that's not recommended for RESTful applications.
  # Note: This route will make all actions in every controller accessible via GET requests.
  # match ':controller(/:action(/:id))(.:format)'
end
