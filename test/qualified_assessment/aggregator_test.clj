(ns qualified-assessment.aggregator-test
  (:require [qualified-assessment.aggregator :as aggregator]
            [clojure.test :refer :all]))

(def sample-event {:date "2011-12-03T10:15:30Z",
                   :amount 4285,
                   :payment-method "SLICE_IT",
                   :merchant-id "1bb53ed1-787b-4543-9def-ea18eef7902e"})

(deftest aggregrate
  (testing "One event"
    (is (= [{:datapoint "2011-12-03:10|10-50", :events 1}
            {:datapoint "2011-12-03:10|10-50|SLICE_IT", :events 1}
            {:datapoint "10-50|SLICE_IT", :events 1}
            {:datapoint "2011-12-03|1bb53ed1-787b-4543-9def-ea18eef7902e", :events 1}
            {:datapoint "1bb53ed1-787b-4543-9def-ea18eef7902e|SLICE_IT", :events 1}]
           (aggregator/aggregate [sample-event]))))

  (testing "Repeated event"
    (is (= [{:datapoint "2011-12-03:10|10-50", :events 2}
            {:datapoint "2011-12-03:10|10-50|SLICE_IT", :events 2}
            {:datapoint "10-50|SLICE_IT", :events 2}
            {:datapoint "2011-12-03|1bb53ed1-787b-4543-9def-ea18eef7902e", :events 2}
            {:datapoint "1bb53ed1-787b-4543-9def-ea18eef7902e|SLICE_IT", :events 2}]
           (aggregator/aggregate [sample-event sample-event]))))

  (testing "One merchant, different hours, amounts, and payment methods"
    (is (= (sort-by :datapoint
                    [{:datapoint "10-50|PAY_NOW", :events 1}
                     {:datapoint "10-50|SLICE_IT", :events 1}
                     {:datapoint
                      "1bb53ed1-787b-4543-9def-ea18eef7902e|PAY_LATER",
                      :events 1}
                     {:datapoint "1bb53ed1-787b-4543-9def-ea18eef7902e|PAY_NOW",
                      :events 2}
                     {:datapoint "1bb53ed1-787b-4543-9def-ea18eef7902e|SLICE_IT",
                      :events 1}
                     {:datapoint "2011-12-03:10|10-50", :events 1}
                     {:datapoint "2011-12-03:10|10-50|SLICE_IT", :events 1}
                     {:datapoint "2011-12-03:12|10-50", :events 1}
                     {:datapoint "2011-12-03:12|10-50|PAY_NOW", :events 1}
                     {:datapoint "2011-12-03:14|<10", :events 1}
                     {:datapoint "2011-12-03:14|<10|PAY_NOW", :events 1}
                     {:datapoint
                      "2011-12-03|1bb53ed1-787b-4543-9def-ea18eef7902e",
                      :events 3}
                     {:datapoint "2011-12-04:10|>500", :events 1}
                     {:datapoint "2011-12-04:10|>500|PAY_LATER", :events 1}
                     {:datapoint
                      "2011-12-04|1bb53ed1-787b-4543-9def-ea18eef7902e",
                      :events 1}
                     {:datapoint "<10|PAY_NOW", :events 1}
                     {:datapoint ">500|PAY_LATER", :events 1}])
           (sort-by :datapoint
                    (aggregator/aggregate [{:date "2011-12-03T10:15:30Z",
                                            :amount 4285,
                                            :payment-method "SLICE_IT",
                                            :merchant-id "1bb53ed1-787b-4543-9def-ea18eef7902e"}
                                           {:date "2011-12-03T12:15:30Z",
                                            :amount 1142,
                                            :payment-method "PAY_NOW",
                                            :merchant-id "1bb53ed1-787b-4543-9def-ea18eef7902e"}
                                           {:date "2011-12-03T14:15:30Z",
                                            :amount 185,
                                            :payment-method "PAY_NOW",
                                            :merchant-id "1bb53ed1-787b-4543-9def-ea18eef7902e"}
                                           {:date "2011-12-04T10:15:30Z",
                                            :amount 82850,
                                            :payment-method "PAY_LATER",
                                            :merchant-id "1bb53ed1-787b-4543-9def-ea18eef7902e"}]))))))
