(ns rally.api.request-test
  (:require [rally.api.request :as request]
            [clojure.test :refer :all])
  (:import [java.net URI]))

(deftest set-uri-should-create-valid-urls
  (let [rest-api {:rally {:host "http://localhost:7001", :version :v2.0}}]
    (is (= "http://localhost:7001/slm/webservice/v2.0/HierarchicalRequirement" (request/get-url (request/set-uri rest-api :userstory))))
    (is (= "http://localhost:7001/slm/webservice/v2.0/Defect/create" (request/get-url (request/set-uri rest-api :defect "create"))))
    (is (= "http://localhost:7001/slm/webservice/v2.0/Defect/1234" (request/get-url (request/set-uri rest-api :defect 1234))))    
    (is (= "http://localhost:7001/slm/webservice/v2.0/Defect/create/me" (request/get-url (request/set-uri rest-api :defect "create" "me"))))
    (is (= "http://localhost:7001/slm/webservice/v2.0/security/authorize" (request/get-url (request/set-uri rest-api :security :authorize))))))

(deftest set-url-should-translate-objects-to-refs
  (let [rest-api {:rally {:host "http://localhost:7001" :version :v2.0}}]
    (is (= "http://localhost:7001/slm/webservice/v2.0/Defect" (request/get-url (request/set-url rest-api "http://localhost:7001/slm/webservice/v2.0/Defect"))))
    (is (= "http://localhost:7001/slm/webservice/v2.0/Defect" (request/get-url (request/set-url rest-api {:metadata/ref "http://localhost:7001/slm/webservice/v2.0/Defect"}))))))

(deftest set-body-as-map-should-create-valid-body
  (is (= "{\"Defect\":{\"Name\":\"Jane\"}}" (request/get-body (request/set-body-as-map {} :defect {:name "Jane"}))))
  (is (= "{\"HierarchicalRequirement\":{\"Name\":\"Jane\"}}" (request/get-body (request/set-body-as-map {} :userstory {:name "Jane"})))))

(deftest merge-query-params-should-translate-values
  (let [rest-api {:request {:query-params {:project 123, :start 1, :pagesize 20}}}]
    (is (= "(Name = \"Jane\")" (request/get-query-param (request/merge-query-params rest-api {:query [:= :name "Jane"]}) :query)))
    (is (= "Name,Description" (request/get-query-param (request/merge-query-params rest-api {:fetch [:name :description]}) :fetch)))
    (is (= "Name desc" (request/get-query-param (request/merge-query-params rest-api {:order [[:name :desc]]}) :order)))
    (is (= 20 (request/get-query-param (request/merge-query-params rest-api {}) :pagesize)))))

