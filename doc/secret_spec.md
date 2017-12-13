```clojure

(def Secret
  (either
    {:plain Str}                    ;   as plain text
    {:password-store-single Str}    ;   as password store key wo linebreaks & whitespaces
    {:password-store-record         ;   as password store entry containing login (record :login)
      {:path Str,                   ;      and password (no field or :password)
       :element (enum :password :login)}}
    {:password-store-multi Str}     ;   as password store key with linebreaks
    {:pallet-secret {:key-id Str,
                    :service-path [Keyword],
                    :record-element (enum :secret :account)}})
```
