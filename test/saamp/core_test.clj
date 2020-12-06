(ns saamp.core-test
  (:require [clojure.test :refer :all]
            [saamp.core :refer :all]))

(deftest blah
    (is (get-meal #{[50 50] [100 200]} [50 50]) [50 50]))
