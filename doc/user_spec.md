```clojure
(def User                           ; see dda-user-crate
  {:password Secret,
   :name Str,
   (optional-key :gpg) {:gpg-passphrase Secret
                        :gpg-public-key Secret
                        :gpg-private-key Secret}
   (optional-key :ssh) {:ssh-private-key Secret
                        :ssh-public-key Secret}})
```
