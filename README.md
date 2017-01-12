# NFCToken
Android application for signing messages and sending them over NFC, receiving messages and verify the digital signature.

Files of interest:
Under directory \app\src\main\java\com\tokens\nfc\nfctokens

  - ArchiveManager.java - Responsible for reading/writing file, endoding/decoding, signing/verifying signatures
  
  - DetailsActivity.java - Activity for detialed view
  
  - ListingFragment.java - Fragment class for Listing view
  
  - MainActivity.java
  
  - PageAdapter.java - Adapter for the ViewPager in MainActivity
  
  - Record.java - Wrapper representing a record entry
  
  - RecordArrayAdapter.java - ArrayAdapter for the ListView
  
  - SendFragment.java - Fragment for the send message view
  
Under directory \app\src\main\res\layout

  - activity_detials.xml - Layout file for DetialsActivity
  
  - listing_fragment.xml - Layout file for ListingFragment
  
  - list_item.xml - Layout file for an individual item in the ListView
  
  - main_pager.xml - Layout file for MainActivity
  
  - send_fragment.xml - Layout file for SendFragment
