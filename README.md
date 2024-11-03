# WhatsApp Chat Parser for Transaction Tracking

## Overview
This app parses a WhatsApp chat used as a ledger for tracking transactions between people. It's designed to automate the process of reviewing group chats where participants record payments, allowing for easy end-of-month calculations to determine outstanding balances.

## Features
- Reads and parses exported WhatsApp chat data.
- Sums the numbers present in messages to show who owes whom.
- Simple rules for data entry ensure accurate balance tracking.

## How It Works
1. **Chat Export**: Export the relevant WhatsApp chat and provide it as input to the app.
2. **Transaction Parsing**:
   - Each message should contain only one number.
   - The sender of the message is considered to have "lent" money to other person.
3. **Calculations**: The app sums the amounts and shows the balances, making it easy to track who owes whom.

## User Guidelines
- **One number per message**: Ensure that each message contains only a single number representing the amount.
- **Sender responsibility**: The sender of the message is assumed to have paid or "lent" the stated amount to the other person.

## Usage Steps
1. Export the WhatsApp chat.
2. Run the app with the exported zip file.
3. Review the calculated totals to determine the end-of-period balances.

## Notes
- The app relies on users following the outlined rules for accurate parsing.
- Users should confirm that messages are formatted correctly to avoid discrepancies.

## License
Feel free to use, modify, and contribute to this app as needed.
