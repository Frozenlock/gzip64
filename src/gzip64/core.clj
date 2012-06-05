(ns gzip64.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.codec.base64 :as b64])
  (:import java.util.zip.GZIPInputStream
           java.util.zip.GZIPOutputStream))

(defn gunzip
  "Writes the contents of input to output, decompressed.

  input: something which can be opened by io/input-stream.
      The bytes supplied by the resulting stream must be gzip compressed.
  output: something which can be copied to by io/copy."
  [input output & opts]
  (with-open [input (-> input io/input-stream GZIPInputStream.)]
    (apply io/copy input output opts)))

(defn gzip
  "Writes the contents of input to output, compressed.

  input: something which can be copied from by io/copy.
  output: something which can be opend by io/output-stream.
      The bytes written to the resulting stream will be gzip compressed."
  [input output & opts]
  (with-open [output (-> output  GZIPOutputStream.)]
    (apply io/copy input output opts)))

(defn get-gzipped [string]
  "Gzip the string and return a byte-array"
  (let [gzipped (java.io.ByteArrayOutputStream.)]
    (gzip (io/input-stream (.getBytes string)) gzipped)
    (.toByteArray gzipped)))


(defn get-gunzipped [byte-array]
  "Gunzip the byte-array and return a string"
  (let [gunzipped (java.io.ByteArrayOutputStream.)]
    (gunzip (io/input-stream byte-array) gunzipped)
    (String. (.toByteArray gunzipped))))

(defn encode-base-64 [byte-array]
  (String. (b64/encode byte-array)))

(defn decode-base-64 [string]
  (b64/decode (.getBytes string)))

(defn gz64 [string]
  "Gzip the string, then encode it in base 64 for easy web transfer"
  (encode-base-64 (get-gzipped string)))

(defn gunz64 [string]
  "Decode the base 64 string, then gunzip it and return a string"
  (get-gunzipped (decode-base-64 string)))