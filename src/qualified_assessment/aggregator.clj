(ns qualified-assessment.aggregator
  (:require [clojure.string :as string]))

(defn ->amount-bracket [event]
  (let [amount (/ (:amount event) 100.00)]
    (cond
      (< amount 10.0)    "<10"
      (and
       (<= 10.0 amount)
       (< amount 50.0))  "10-50"
      (and
       (<= 50.0 amount)
       (< amount 100.0)) "50-100"
      (and
       (<= 100.0 amount)
       (< amount 500.0)) "100-500"
      :else              ">500")))


(defn- datapoint-amount-bracket-per-hour
  [event]
  {:pre [(and (not (nil? (:date event)))
              (not (nil? (:amount event))))]}
  (str (string/replace (:date event) #"^(.+)T(\d+):.+$" "$1:$2") "|"
       (->amount-bracket event)))

(defn- datapoint-amount-bracket-n-payment-method-per-hour
  [event]
  {:pre [(and (not (nil? (:date event)))
              (not (nil? (:amount event)))
              (not (nil? (:payment-method event))))]}
  (str (string/replace (:date event) #"^(.+)T(\d+):.+$" "$1:$2") "|"
       (->amount-bracket event) "|"
       (:payment-method event)))

(defn- datapoint-amount-bracket-for-payment-method
  [event]
  (:pre [(and
          (not (nil? (:amoount event)))
          (not (nil? (:payment-method event))))])
  (str (->amount-bracket event) "|"
       (:payment-method event)))

(defn- datapoint-merchant-payment-per-day
  [event]
  {:pre [(and
          (not (nil? (:date event)))
          (not (nil? (:merchant-id event) )))]}
  (str (string/replace (:date event) #"T.+$" "") "|"
       (:merchant-id event)))

(defn- datapoint-merchant-payment-per-payment-method
  [event]
  {:pre [(and
          (not (nil? (:merchant-id event)))
          (not (nil? (:payment-method event))))]}
  (str (:merchant-id event) "|"
       (:payment-method event)))

(defn aggregate-datapoints
  [events]
  (reduce (fn [aggregates event]
            (-> aggregates
                (update (datapoint-amount-bracket-per-hour event)
                        #((fnil inc 0) %))
                (update (datapoint-amount-bracket-n-payment-method-per-hour event)
                        #((fnil inc 0) %))                              
                (update (datapoint-amount-bracket-for-payment-method event)
                        #((fnil inc 0) %))
                (update (datapoint-merchant-payment-per-day event)
                        #((fnil inc 0) %))
                (update (datapoint-merchant-payment-per-payment-method event) 
                        #((fnil inc 0) %))))
          {} events))

(defn decorate-aggregate
  [aggregate]
  (reduce (fn [decorated-aggregate [datapoint events]]
            (conj decorated-aggregate {:datapoint datapoint :events events}))
          [] aggregate))

(defn aggregate [events]
  (->> events
       aggregate-datapoints
       decorate-aggregate))
