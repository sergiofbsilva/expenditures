/*
 * @(#)SyncSuppliersAux.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz, Nuno Ochoa, Paulo Abrantes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Expenditure Tracking Module.
 *
 *   The Expenditure Tracking Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Expenditure Tracking Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Expenditure Tracking Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.expenditureTrackingSystem.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import module.finance.domain.SupplierContact;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.util.Money;
import pt.ist.bennu.core.util.TransactionalThread;
import pt.ist.bennu.core.util.legacy.Address;
import pt.ist.dbUtils.ExternalDbOperation;
import pt.ist.dbUtils.ExternalDbQuery;
import pt.ist.expenditureTrackingSystem.domain.organization.Supplier;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class SyncSuppliersAux {

    public static class GiafCountry {
        public String pais_cod_pai;
        public String pais_dsg_com;
        public String pais_dsg_abv;
        public String pais_cod_idi;
        public String pais_cod_moe;
        public String pais_flg_cee;
        public String pais_dsg_iva;
        public String pais_pai_iva;
        public String pais_num_dig;
        public String pais_flg_val;
        public String pais_num_frm;
        public String pais_portugal;
        public String tipo_cod_post;
        public String num_digito_cod_post;
        public String iso_3166;
        public String iso_4217;
        public String utilizador_criacao;
        public String utilizador_alteracao;
        public String data_criacao;
        public String data_alteracao;
        public String pais_dsg_ing;
        public String pais_area_geo;

        GiafCountry(final ResultSet resultSet) throws SQLException {
            pais_cod_pai = get(resultSet, 1);
            pais_dsg_com = get(resultSet, 2);
            pais_dsg_abv = get(resultSet, 3);
            pais_cod_idi = get(resultSet, 4);
            pais_cod_moe = get(resultSet, 5);
            pais_flg_cee = get(resultSet, 6);
            pais_dsg_iva = get(resultSet, 7);
            pais_pai_iva = get(resultSet, 8);
            pais_num_dig = get(resultSet, 9);
            pais_flg_val = get(resultSet, 10);
            pais_num_frm = get(resultSet, 11);
            pais_portugal = get(resultSet, 12);
            tipo_cod_post = get(resultSet, 13);
            num_digito_cod_post = get(resultSet, 14);
            iso_3166 = get(resultSet, 15);
            iso_4217 = get(resultSet, 16);
            utilizador_criacao = get(resultSet, 17);
            utilizador_alteracao = get(resultSet, 18);
            data_criacao = get(resultSet, 19);
            data_alteracao = get(resultSet, 20);
            pais_dsg_ing = get(resultSet, 21);
            pais_area_geo = get(resultSet, 22);
        }

        private String get(final ResultSet resultSet, final int i) throws SQLException {
            final String string = resultSet.getString(i);
            return string == null || string.length() == 0 ? " " : string.replace('\n', ' ').replace('\t', ' ');
        }

    }

    public static class GiafAddress {
        public String ruaEnt = " ";
        public String locEnt = " ";
        public String codPos = " ";
        public String codPai = " ";
        public String telEnt = " ";
        public String faxEnt = " ";
        public String email = " ";

        private Address address = null;

        public GiafAddress(final ResultSet resultSet) throws SQLException {
            ruaEnt = get(resultSet, 2);
            locEnt = get(resultSet, 3);
            codPos = get(resultSet, 4);
            codPai = get(resultSet, 5);
            telEnt = get(resultSet, 6);
            faxEnt = get(resultSet, 7);
            email = get(resultSet, 8);
        }

        private String get(final ResultSet resultSet, final int i) throws SQLException {
            final String string = resultSet.getString(i);
            return string == null || string.length() == 0 ? " " : string.replace('\n', ' ').replace('\t', ' ');
        }

        public Address getAddress(final Set<GiafCountry> giafCountries) {
            if (address == null) {
                final String country = getCountry(giafCountries, this);
                address = new Address(ruaEnt, null, codPos, locEnt, country);
            }
            return address;
        }
    }

    public static class GiafSupplier {
        public String codEnt;
        public String numFis;
        public String nom_ent;
        public String nom_ent_abv;
        public boolean canceled = false;

        final Collection<GiafAddress> addresses = new ArrayList<GiafAddress>();

        GiafSupplier(final ResultSet resultSet) throws SQLException {
            codEnt = get(resultSet, 1);
            numFis = get(resultSet, 2);
            nom_ent = get(resultSet, 3);
            nom_ent_abv = get(resultSet, 4);
        }

        public void setContactInformation(final ResultSet resultSet) throws SQLException {
            addresses.add(new GiafAddress(resultSet));
        }

        private String get(final ResultSet resultSet, final int i) throws SQLException {
            final String string = resultSet.getString(i);
            return string == null || string.length() == 0 ? " " : string.replace('\n', ' ').replace('\t', ' ');
        }
    }

    public static class SupplierMap {

        public Map<String, GiafSupplier> giafCodEntMap = new HashMap<String, GiafSupplier>();
        public Map<String, GiafSupplier> giafFiscalIdMap = new HashMap<String, GiafSupplier>();

        public void index(final GiafSupplier giafSupplier) {
            giafCodEntMap.put(giafSupplier.codEnt, giafSupplier);
            giafFiscalIdMap.put(giafSupplier.numFis, giafSupplier);
        }

        public GiafSupplier getGiafSupplierByFiscalId(final String fiscalIdentificationCode) {
            return giafFiscalIdMap.get(fiscalIdentificationCode);
        }

        public GiafSupplier getGiafSupplierByGiafKey(final String giafKey) {
            return giafCodEntMap.get(giafKey);
        }

        public Collection<GiafSupplier> getGiafSuppliers() {
            return giafCodEntMap.values();
        }

        public Collection<Collection<GiafSupplier>> getGiafSuppliersChunks() {
            final Collection<Collection<GiafSupplier>> result = new ArrayList<Collection<GiafSupplier>>();
            Collection<GiafSupplier> chunk = new ArrayList<GiafSupplier>();
            result.add(chunk);
            for (final GiafSupplier giafSuppliers : giafCodEntMap.values()) {
                if (chunk.size() == 50) {
                    chunk = new ArrayList<GiafSupplier>();
                    result.add(chunk);
                }
                chunk.add(giafSuppliers);
            }
            return result;
        }
    }

    private static class CountryQuery implements ExternalDbQuery {

        private final Set<GiafCountry> giafCountries;

        public CountryQuery(final Set<GiafCountry> giafCountries) {
            this.giafCountries = giafCountries;
        }

        @Override
        public String getQueryString() {
            return "SELECT " + "a.pais_cod_pai," + "a.pais_dsg_com," + "a.pais_dsg_abv," + "a.pais_cod_idi," + "a.pais_cod_moe,"
                    + "a.pais_flg_cee," + "a.pais_dsg_iva," + "a.pais_pai_iva," + "a.pais_num_dig," + "a.pais_flg_val,"
                    + "a.pais_num_frm," + "a.pais_portugal," + "a.tipo_cod_post," + "a.num_digito_cod_post," + "a.iso_3166,"
                    + "a.iso_4217," + "a.utilizador_criacao," + "a.utilizador_alteracao," + "a.data_criacao,"
                    + "a.data_alteracao," + "a.pais_dsg_ing," + "a.pais_area_geo" + " FROM gidpaises a";
        }

        @Override
        public void processResultSet(final ResultSet resultSet) throws SQLException {
            while (resultSet.next()) {
                final GiafCountry giafCountry = new GiafCountry(resultSet);
                giafCountries.add(giafCountry);
            }
        }

    }

    private static class SupplierQuery implements ExternalDbQuery {

        private final SupplierMap supplierMap;

        SupplierQuery(final SupplierMap supplierMap) {
            this.supplierMap = supplierMap;
        }

        @Override
        public String getQueryString() {
            return "SELECT GIDFORN.forn_cod_ent, GIDENTGER.num_fis, GIDENTGER.nom_ent, GIDENTGER.nom_ent_abv"
                    + " FROM GIDFORN, GIDENTGER where GIDFORN.forn_cod_ent = GIDENTGER.cod_ent";
        }

        @Override
        public void processResultSet(final ResultSet resultSet) throws SQLException {
            while (resultSet.next()) {
                final GiafSupplier giafSupplier = new GiafSupplier(resultSet);
                supplierMap.index(giafSupplier);
            }
        }

    }

    private static class SupplierContactQuery implements ExternalDbQuery {

        private final SupplierMap supplierMap;

        SupplierContactQuery(final SupplierMap supplierMap) {
            this.supplierMap = supplierMap;
        }

        @Override
        public String getQueryString() {
            return "SELECT cod_ent, rua_ent, loc_ent, cod_pos, cod_pai, tel_ent, fax_ent, email, num_mor FROM GIDMORADA";
        }

        @Override
        public void processResultSet(final ResultSet resultSet) throws SQLException {
            while (resultSet.next()) {
                final String codEnt = resultSet.getString(1);
                final GiafSupplier giafSupplier = findRemoteSupplier(codEnt);
                if (giafSupplier != null) {
                    giafSupplier.setContactInformation(resultSet);
                }
            }
        }

        private GiafSupplier findRemoteSupplier(final String codEnt) {
            for (final GiafSupplier giafSupplier : supplierMap.getGiafSuppliers()) {
                if (giafSupplier.codEnt.equals(codEnt)) {
                    return giafSupplier;
                }
            }
            return null;
        }

    }

    private static class CanceledSupplierQuery implements ExternalDbQuery {

        private final SupplierMap supplierMap;

        CanceledSupplierQuery(final SupplierMap supplierMap) {
            this.supplierMap = supplierMap;
        }

        @Override
        public String getQueryString() {
            return "select * from (" + "SELECT ENTC_COD_ENT, max(ENTC_DAT_CAN) as cancelamento, max(ENTC_DAT_ACT) as activacao "
                    + "FROM GIDENTCAN group by ENTC_COD_ENT) " + "where activacao is null or activacao < cancelamento";
        }

        @Override
        public void processResultSet(final ResultSet resultSet) throws SQLException {
            while (resultSet.next()) {
                final String codEnt = resultSet.getString(1);
                final GiafSupplier giafSupplier = findRemoteSupplier(codEnt);
                if (giafSupplier != null) {
                    giafSupplier.canceled = true;
                }
            }
        }

        private GiafSupplier findRemoteSupplier(final String codEnt) {
            for (final GiafSupplier giafSupplier : supplierMap.getGiafSuppliers()) {
                if (giafSupplier.codEnt.equals(codEnt)) {
                    return giafSupplier;
                }
            }
            return null;
        }

    }

    public static class CountryReader extends ExternalDbOperation {

        private final Set<GiafCountry> giafCountries;

        public CountryReader(final Set<GiafCountry> giafCountries) {
            this.giafCountries = giafCountries;
        }

        @Override
        protected String getDbPropertyPrefix() {
            return "db.giaf";
        }

        @Override
        protected void doOperation() throws SQLException {
            final CountryQuery countryQuery = new CountryQuery(giafCountries);
            executeQuery(countryQuery);
        }

    }

    public static class SupplierReader extends ExternalDbOperation {

        private final SupplierMap supplierMap;

        public SupplierReader(final SupplierMap supplierMap) {
            this.supplierMap = supplierMap;
        }

        @Override
        protected String getDbPropertyPrefix() {
            return "db.giaf";
        }

        @Override
        protected void doOperation() throws SQLException {
            final SupplierQuery supplierQuery = new SupplierQuery(supplierMap);
            executeQuery(supplierQuery);

            final SupplierContactQuery supplierContactQuery = new SupplierContactQuery(supplierMap);
            executeQuery(supplierContactQuery);

            final CanceledSupplierQuery canceledSupplierQuery = new CanceledSupplierQuery(supplierMap);
            executeQuery(canceledSupplierQuery);
        }

    }

    private static void closeLocalSuppliers(final Set<Supplier> suppliersFromGiaf) {
        for (final Supplier supplier : MyOrg.getInstance().getSuppliersSet()) {
            if (!suppliersFromGiaf.contains(supplier)) {
                if (!supplier.getSupplierLimit().equals(Money.ZERO)) {
                    supplier.setSupplierLimit(Money.ZERO);
                }
            }
        }
    }

    private static boolean isAllocatedAmountNull(final Supplier supplier) {
        boolean result = true;
        for (final VirtualHost virtualHost : MyOrg.getInstance().getVirtualHostsSet()) {
            try {
                VirtualHost.setVirtualHostForThread(virtualHost);
                result &= supplier.getTotalAllocated().isZero();
            } finally {
                VirtualHost.releaseVirtualHostFromThread();
            }
        }
        return result;
    }

    private static boolean shouldDiscard(final GiafSupplier giafSupplier) {
        final String giafKey = giafSupplier.codEnt;
        return (giafKey.length() == 6 && giafKey.charAt(0) == '1') || (giafKey.length() == 10 && giafKey.charAt(0) == 'E');
    }

    private static void updateSupplierInformation(final Set<GiafCountry> giafCountries, final Supplier supplier,
            final GiafSupplier giafSupplier) {
        if (giafSupplier.canceled || shouldDiscard(giafSupplier)) {
            updateSupplierInformationAux(giafCountries, supplier, giafSupplier);
            if (!supplier.getSupplierLimit().equals(Money.ZERO)) {
                supplier.setSupplierLimit(Money.ZERO);
            }
            //}
        } else {
            updateSupplierInformationAux(giafCountries, supplier, giafSupplier);
            if (supplier.getSupplierLimit().equals(Money.ZERO)) {
                supplier.setSupplierLimit(Supplier.SOFT_SUPPLIER_LIMIT);
            }
        }
    }

    private static void updateSupplierInformationAux(final Set<GiafCountry> giafCountries, final Supplier supplier,
            final GiafSupplier giafSupplier) {
        if (!giafSupplier.numFis.equals(supplier.getFiscalIdentificationCode())) {
            supplier.setFiscalIdentificationCode(giafSupplier.numFis);
        }
        if (!giafSupplier.nom_ent.equals(supplier.getName())) {
            supplier.setName(giafSupplier.nom_ent);
        }
        if (!giafSupplier.nom_ent_abv.equals(supplier.getAbbreviatedName())) {
            supplier.setAbbreviatedName(giafSupplier.nom_ent_abv);
        }
        updateAddressInformation(giafCountries, supplier, giafSupplier);
    }

    private static void updateAddressInformation(final Set<GiafCountry> giafCountries, final Supplier supplier,
            final GiafSupplier giafSupplier) {
        for (final GiafAddress giafAddress : giafSupplier.addresses) {
            final Address address = giafAddress.getAddress(giafCountries);
            supplier.registerContact(address, giafAddress.telEnt, giafAddress.faxEnt, giafAddress.email);
        }
        for (final SupplierContact contact : supplier.getSupplierContactSet()) {
            if (!containsContact(giafSupplier, contact)) {
                contact.delete();
            }
        }
    }

    private static boolean containsContact(final GiafSupplier giafSupplier, SupplierContact contact) {
        for (final GiafAddress giafAddress : giafSupplier.addresses) {
            final Address address = giafAddress.address;
            if (address.equals(contact.getAddress()) && giafAddress.telEnt.equals(contact.getPhone())
                    && giafAddress.faxEnt.equals(contact.getFax()) && giafAddress.email.equals(contact.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private static Supplier findSupplierByGiafKey(final String codEnt) {
        for (final Supplier supplier : MyOrg.getInstance().getSuppliersSet()) {
            final String giafKey = supplier.getGiafKey();
            if (codEnt.equals(giafKey)) {
                return supplier;
            }
        }
        return null;
    }

    private static String getCountry(final Set<GiafCountry> giafCountries, final GiafAddress giafAddress) {
        if (giafAddress.codPai != null) {
            for (final GiafCountry giafCountry : giafCountries) {
                if (giafAddress.codPai.equals(giafCountry.pais_cod_pai)) {
                    return giafCountry.pais_dsg_com;
                }
            }
        }
        return "?";
        // return giafSupplier.codPai != null && giafSupplier.codPai.equals("P")
        // ? "Portugal" : "?";
    }

    private static class UodateSuppliersThread extends TransactionalThread {

        private final Collection<GiafSupplier> giafSuppliers;
        private final Set<GiafCountry> giafCountries;

        int matched = 0;
        int created = 0;
        int discarded = 0;

        UodateSuppliersThread(final Collection<GiafSupplier> giafSuppliers, final Set<GiafCountry> giafCountries) {
            this.giafSuppliers = giafSuppliers;
            this.giafCountries = giafCountries;
        }

        @Override
        public void transactionalRun() {
            for (final GiafSupplier giafSupplier : giafSuppliers) {
                if (giafSupplier.canceled || shouldDiscard(giafSupplier)) {
                    discarded++;
                }
                Supplier supplier = findSupplierByGiafKey(giafSupplier.codEnt);
                if (supplier == null && !giafSupplier.canceled && !shouldDiscard(giafSupplier)) {
                    supplier = new Supplier(giafSupplier.nom_ent, giafSupplier.nom_ent_abv, giafSupplier.numFis, null);
                    supplier.setGiafKey(giafSupplier.codEnt);
                    updateAddressInformation(giafCountries, supplier, giafSupplier);
                    created++;
                } else if (supplier != null) {
                    matched++;
                    updateSupplierInformation(giafCountries, supplier, giafSupplier);
                }
            }
        }

    }

    private static class CloseSuppliersThread extends TransactionalThread {

        private final Set<Supplier> suppliersToClose;

        CloseSuppliersThread(final Set<Supplier> suppliersToClose) {
            this.suppliersToClose = suppliersToClose;
        }

        @Override
        public void transactionalRun() {
            for (final Supplier supplier : suppliersToClose) {
                if (!supplier.getSupplierLimit().equals(Money.ZERO)) {
                    supplier.setSupplierLimit(Money.ZERO);
                }
            }
        }

    }

    public static void syncData() {
        final Set<GiafCountry> giafCountries = new HashSet<GiafCountry>();
        final CountryReader countryReader = new CountryReader(giafCountries);
        countryReader.execute();

        final SupplierMap supplierMap = new SupplierMap();
        final SupplierReader supplierReader = new SupplierReader(supplierMap);
        supplierReader.execute();

        final Set<Supplier> suppliersFromGiaf = new HashSet<Supplier>();
        for (final GiafSupplier giafSupplier : supplierMap.getGiafSuppliers()) {
            Supplier supplier = findSupplierByGiafKey(giafSupplier.codEnt);
            if (supplier != null) {
                suppliersFromGiaf.add(supplier);
            }
        }
        final Set<Supplier> suppliersToClose = new HashSet<Supplier>();
        for (final Supplier supplier : MyOrg.getInstance().getSuppliersSet()) {
            if (!suppliersFromGiaf.contains(supplier)) {
                if (!supplier.getSupplierLimit().equals(Money.ZERO)) {
                    suppliersToClose.add(supplier);
                }
            }
        }
        final CloseSuppliersThread closeSuppliersThread = new CloseSuppliersThread(suppliersToClose);
        closeSuppliersThread.start();
        try {
            closeSuppliersThread.join();
        } catch (InterruptedException e) {
            throw new Error(e);
        }

        for (final Collection<GiafSupplier> giafSuppliers : supplierMap.getGiafSuppliersChunks()) {
            final UodateSuppliersThread updateSuppliersThread = new UodateSuppliersThread(giafSuppliers, giafCountries);
            updateSuppliersThread.start();
            try {
                updateSuppliersThread.join();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
    }

}
