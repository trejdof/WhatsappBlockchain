WhatsApp Chat Parser for Transaction Tracking
Overview
This is a simple app designed to parse a WhatsApp chat used as a decentralized ledger for tracking transactions between people. For years, dedicated group chats on WhatsApp were used to record who paid for what, allowing for end-of-month calculations to determine who owes whom.

This app automates that process by parsing exported chat data and summing the amounts written in messages. It simplifies the task of recalculating balances between group members at the end of a set period, such as a month.

How It Works
The app reads an exported WhatsApp chat file.
It scans for numbers in messages and sums them up according to specific rules.
The person who writes a message with a number is considered to have "lent" money to the other members of the chat on that occasion.
User Responsibilities
To ensure accurate parsing, users must follow these simple rules when using WhatsApp as a transaction ledger:

Each message should contain only one number.
The person writing the message is assumed to have paid or "lent" money to the other participants.
Usage
Export the WhatsApp chat from your group conversation.
Run the app with the exported chat as input.
Review the calculated totals to see who owes money and how much.
Note: The accuracy of the app depends on following the outlined rules. Users are responsible for ensuring that only one number is present per message and that the context of each message follows the lending/borrowing convention described.

License
Feel free to use and modify the app as needed.
